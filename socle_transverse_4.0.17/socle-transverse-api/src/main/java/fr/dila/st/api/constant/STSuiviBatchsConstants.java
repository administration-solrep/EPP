package fr.dila.st.api.constant;

public class STSuiviBatchsConstants {

    public enum TypeBatch {
        FONCTIONNEL("Fonctionnel"),
        TECHNIQUE("Technique"),
        MIXTE("Mixte");

        private String label;

        TypeBatch(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static TypeBatch findByName(String name) {
            for (TypeBatch typeBatch : TypeBatch.values()) {
                if (typeBatch.name().equals(name)) {
                    return typeBatch;
                }
            }
            return null;
        }
    }

    private STSuiviBatchsConstants() {}
}
