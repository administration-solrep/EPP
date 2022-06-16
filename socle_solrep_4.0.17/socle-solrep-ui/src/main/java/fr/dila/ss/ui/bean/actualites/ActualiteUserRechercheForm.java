package fr.dila.ss.ui.bean.actualites;

import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_EMISSION;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_HASPJ;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_OBJET;

import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.SwBean;
import fr.dila.st.ui.bean.FormSort;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@SwBean(keepdefaultValue = true)
public class ActualiteUserRechercheForm extends AbstractActualiteRechercheForm {
    @NxProp(
        docType = ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE,
        xpath = ActualiteConstant.ACTUALITE_REQUETE_XPATH_DATE_EMISSION_FIN
    )
    private Calendar dateEmissionFinEffective = DateUtil.localDateToGregorianCalendar(LocalDate.now());

    public ActualiteUserRechercheForm() {
        super(DEFAULT_SIZE);
        this.archivee = true;
    }

    @Override
    public void setDateEmissionFin(Calendar dateEmissionFin) {
        super.setDateEmissionFin(dateEmissionFin);
        if (
            dateEmissionFin != null && DateUtil.gregorianCalendarToLocalDate(dateEmissionFin).isBefore(LocalDate.now())
        ) {
            dateEmissionFinEffective = dateEmissionFin;
        }
    }

    public Calendar getDateEmissionFinEffective() {
        return dateEmissionFinEffective;
    }

    @Override
    protected Map<String, FormSort> getSortForm() {
        Map<String, FormSort> map = new HashMap<>();
        map.put(ACTUALITE_PREFIX_XPATH_DATE_EMISSION, new FormSort(getDateEmissionSort()));
        map.put(ACTUALITE_PREFIX_XPATH_OBJET, new FormSort(getObjetSort()));
        map.put(ACTUALITE_PREFIX_XPATH_HASPJ, new FormSort(getHasPjSort()));
        return map;
    }
}
