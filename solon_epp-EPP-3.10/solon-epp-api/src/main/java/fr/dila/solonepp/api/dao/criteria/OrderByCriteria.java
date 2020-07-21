package fr.dila.solonepp.api.dao.criteria;


public class OrderByCriteria {

    private OrderField field;
    
    private String order;

    /**
     * @return the field
     */
    public OrderField getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(OrderField field) {
        this.field = field;
    }

    /**
     * @return the order
     */
    public String getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(String order) {
        this.order = order;
    }
    
    
    public enum OrderField {

        ID_DOSSIER,
        OBJET_DOSSIER,
        NIVEAU_LECTURE,
        EMETTEUR,
        DESTINATAIRE,
        COPIE,
        TYPE_EVENEMENT,
        VERSION,
        HORODATAGE;

        public String value() {
            return name();
        }

        public static OrderField fromValue(String v) {
            return valueOf(v);
        }
    }
    
}
