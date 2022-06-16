package fr.dila.st.core.requete.recherchechamp;

import fr.dila.st.core.requete.recherchechamp.descriptor.ChampDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class RechercheChampServiceImpl extends DefaultComponent implements RechercheChampService {
    private static final String CHAMP_EP = "champs";

    private Map<String, Map<String, ChampDescriptor>> mapContribution = new HashMap<>();

    @Override
    public void registerContribution(Object contribution, String xp, ComponentInstance component) {
        String contributorName = component.getName().getName();
        Map<String, ChampDescriptor> reqContribution = getOrCreateRequeteurContribution(contributorName);
        if (CHAMP_EP.equals(xp)) {
            doRegisterChamp((ChampDescriptor) contribution, reqContribution);
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String xp, ComponentInstance component) {
        String contributorName = component.getName().getName();
        Map<String, ChampDescriptor> reqContribution = getOrCreateRequeteurContribution(contributorName);
        if (CHAMP_EP.equals(xp)) {
            reqContribution.remove(((ChampDescriptor) contribution).getName());
        }
    }

    private void doRegisterChamp(ChampDescriptor champ, Map<String, ChampDescriptor> reqContribution) {
        verifyMandatoryAttributes(champ);
        fillChampWithParametres(champ);
        reqContribution.put(champ.getName(), champ);
    }

    @Override
    public List<ChampDescriptor> getChamps(String contribName) {
        return mapContribution
            .get(contribName)
            .values()
            .stream()
            .sorted(Comparator.comparing(ChampDescriptor::getName, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
    }

    @Override
    public ChampDescriptor getChamp(String contribName, String name) {
        ChampDescriptor champ = mapContribution.get(contribName).get(name);
        if (champ == null) {
            throw new NuxeoException(
                "Il n'y a pas de champ de recherche correspondant à l'id [" + name + "] dans la contrib " + contribName
            );
        }
        addAdditionalParametersToChamp(champ);
        for (Parametre p : champ.getParametres()) {
            if (("autocomplete").equals(p.getName())) {
                champ.setTypeChamp("TEXT_AUTOCOMPLETE");
                break;
            }
        }
        return champ;
    }

    private void verifyMandatoryAttributes(ChampDescriptor champ) {
        String excMsgFmt = "Attribute [%s] for extension point [" + CHAMP_EP + "] must be set";
        if (StringUtils.isBlank(champ.getName())) {
            throw new NuxeoException(String.format(excMsgFmt, "name"));
        }
        if (StringUtils.isBlank(champ.getLabel())) {
            throw new NuxeoException(String.format(excMsgFmt, "label"));
        }
        if (StringUtils.isBlank(champ.getField())) {
            throw new NuxeoException(String.format(excMsgFmt, "field"));
        }
        if (StringUtils.isBlank(champ.getTypeChamp())) {
            throw new NuxeoException(String.format(excMsgFmt, "typeChamp"));
        }
    }

    private void fillChampWithParametres(ChampDescriptor champ) {
        champ
            .getParametres()
            .addAll(champ.getParametreDescriptors().stream().map(Parametre::asParametre).collect(Collectors.toList()));
    }

    private void addAdditionalParametersToChamp(ChampDescriptor champ) {
        Class<? extends ChampParameter> champParameterKlass = champ.getChampParameterKlass();
        if (champParameterKlass == null) {
            return;
        }

        ChampParameter champParam;
        try {
            champParam = champParameterKlass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                "Service d'alimentation des paramètre du champ " + champParameterKlass + "non trouvé",
                e
            );
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                "Problème d'accès au service d'alimentation des paramètre du champ" + champParameterKlass,
                e
            );
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new NuxeoException(e);
        }
        Map<String, Serializable> additionalParameters = champParam.getAdditionalParameters();
        if (MapUtils.isNotEmpty(additionalParameters)) {
            champ.setParametres(
                additionalParameters
                    .entrySet()
                    .stream()
                    .map(e -> new Parametre(e.getKey(), e.getValue()))
                    .collect(Collectors.toList())
            );
            fillChampWithParametres(champ);
        }
    }

    private Map<String, ChampDescriptor> getOrCreateRequeteurContribution(String contributorName) {
        if (!mapContribution.containsKey(contributorName)) {
            mapContribution.put(contributorName, new HashMap<>());
        }
        return mapContribution.get(contributorName);
    }
}
