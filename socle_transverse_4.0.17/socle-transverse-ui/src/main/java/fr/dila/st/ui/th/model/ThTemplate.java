package fr.dila.st.ui.th.model;

import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.FragmentService;
import fr.dila.st.ui.services.impl.AbstractMenuServiceImpl;
import fr.dila.st.ui.th.annot.Fragment;
import fr.dila.st.ui.th.annot.FragmentContainer;
import fr.dila.st.ui.th.annot.IHM;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Data to process by thymeleaf rendering engine
 *
 * @author SPL
 *
 */
public class ThTemplate {
    private String name;
    private String layout = "";
    private Map<String, Object> data = new HashMap<>();
    private SpecificContext context;

    public ThTemplate() {
        this(null, null);
    }

    public ThTemplate(String name, SpecificContext context) {
        this.name = name;
        this.context = context;
        if (context != null && context.getCopyDataToResponse()) {
            data = this.getContext().getContextData();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public SpecificContext getContext() {
        return context;
    }

    public void setContext(SpecificContext context) {
        this.context = context;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void processData() {
        if (data == null) {
            data = new HashMap<>();
        }

        if (context == null) {
            context = new SpecificContext();
        }

        if (!(this instanceof AjaxLayoutThTemplate || this instanceof AjaxJSONLayoutThTemplate)) {
            UserSessionHelper.putUserSessionParameter(context, SpecificContext.LAST_TEMPLATE, this.getClass());
        }

        // Processing du template de niveau 1 (Celui implémenté)
        processClass(this.getClass(), 1);
    }

    private Map<String, Object> getDataFromService(Fragment fragment) {
        // Récupération du service Nuxeo
        FragmentService service = ServiceUtil.getRequiredService(fragment.service());

        // Récupération des données depuis le service
        if (this.context == null) {
            return service.getData(new SpecificContext());
        } else {
            return service.getData(this.context);
        }
    }

    @SuppressWarnings("unchecked")
    private void processContainer(FragmentContainer container, int level) {
        // Construction du nom du conteneur
        String containerName = "";
        if (StringUtils.isNotEmpty(container.name())) {
            containerName = container.name();
        }
        containerName += "_blocks";

        List<FragmentBlock> blockList = new ArrayList<>();
        // Get existing block list for override container
        if (data.get(containerName) != null) {
            blockList = (List<FragmentBlock>) data.get(containerName);
        }

        for (Fragment fragment : container.value()) {
            // Création du block pour layout
            FragmentBlock block = new FragmentBlock();
            block.setFilename(fragment.templateFile());
            block.setName(fragment.template());
            block.setOrder(fragment.order());
            block.setLevel(level);

            // Si le block n'a pas déjà été créé par un enfant, on ajoute à la liste et on charge ses données depuis le
            // service
            if (blockList.contains(block)) {
                blockList.remove(block);
            }

            blockList.add(block);
            getDataFromService(fragment).forEach(data::put);
        }

        // Trie de la liste pour prendre en compte l'ordre et le niveau de chaque block
        Collections.sort(blockList);

        // Ajout de la liste aux données
        data.put(containerName, blockList);
    }

    private void processClass(Class<?> clazz, int level) {
        // Si le menu n'a pas encore été setté par un enfant et que j'ai un menu de déclaré je le set.
        // Sinon je laisse le menu de mon enfant sans le surcharger
        if (
            context.getContextData().get(AbstractMenuServiceImpl.CURRENT_KEY) == null &&
            clazz.getDeclaredAnnotation(IHM.class) != null
        ) {
            IHM container = clazz.getDeclaredAnnotation(IHM.class);
            if (container.menu() != null && container.menu().length > 0) {
                context.getContextData().put(AbstractMenuServiceImpl.CURRENT_KEY, container.menu());
            }
        }

        // Si j'extends un autre template je process ma super classe
        // et j'augmente le niveau
        if (clazz.getSuperclass() != null) {
            processClass(clazz.getSuperclass(), level + 1);
        }

        // Vérification que la classe à des annotations
        if (clazz.getDeclaredAnnotations() != null) {
            // Traitement des annotations
            for (Annotation annotation : clazz.getDeclaredAnnotations()) {
                if (annotation instanceof IHM) {
                    // Cas ou nous avons plusieurs conteneurs
                    IHM container = (IHM) annotation;
                    for (FragmentContainer parts : container.value()) {
                        // On process chaque conteneur
                        processContainer(parts, level);
                    }
                    // On stop parce qu'on peut pas avoir plusieurs annotations IHM et quelle encapsule les autres
                    // annotations
                    break;
                } else if (annotation instanceof FragmentContainer) {
                    // Cas ou nous avons un seul conteneur
                    // Ou plusieurs fragments sans conteneur (regroupés dans un conteneur par défaut)
                    FragmentContainer container = (FragmentContainer) annotation;

                    // On process le conteneur
                    processContainer(container, level);

                    // On stop parce qu'avec plusieurs conteneurs on aurait une IHM
                    // Et elle encapsule les fragments si ils sont plusieurs sans conteneur
                    break;
                } else if (annotation instanceof Fragment) {
                    // Cas ou nous avons un seul Fragment
                    Fragment fragment = (Fragment) annotation;

                    // Chargement des données nécessaires au fragment sans construire de block
                    getDataFromService(fragment).forEach(data::putIfAbsent);

                    // On stop parce qu'avec plusieurs fragments on aurait un conteneur
                    break;
                }
            }
        }
    }
}
