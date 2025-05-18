package it.trenical.common;

public class PromotionData implements Promotion {

    private final String code;
    private final String name;
    private final String description;
    private final boolean onlyFidelityUser;
    private final float discount;

    protected PromotionData(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.onlyFidelityUser = builder.onlyFidelityUser;
        this.discount = builder.discount;
    }

    public static Builder newBuilder(String code) {
        return new Builder(code);
    }

    public static class Builder {
        private final String code;
        private String name;
        private String description;
        private boolean onlyFidelityUser;
        private float discount;

        protected Builder(String code) {
            this.code = code;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setOnlyFidelityUser(boolean onlyFidelityUser) {
            this.onlyFidelityUser = onlyFidelityUser;
        }

        public void setDiscount(float discount) {
            this.discount = discount;
        }

        public PromotionData build() {
            return new PromotionData(this);
        }
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isOnlyFidelityUser() {
        return onlyFidelityUser;
    }

    @Override
    public float getDiscount() {
        return discount;
    }

}
