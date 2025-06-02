package it.trenical.common;

public class PromotionData implements Promotion {

    private final String code;
    private final String name;
    private final String description;
    private final boolean onlyFidelityUser;
    private final float discount;

    private PromotionData(Builder builder) {
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

        private Builder(String code) {
            if(code == null) throw new IllegalArgumentException("code cannot be null");
            this.code = code.trim().toUpperCase();
        }

        public Builder setName(String name) {
            this.name = name.trim();
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description.trim();
            return this;
        }

        public Builder setOnlyFidelityUser(boolean onlyFidelityUser) {
            this.onlyFidelityUser = onlyFidelityUser;
            return this;
        }

        public Builder setDiscount(float discount) {
            this.discount = discount;
            return this;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromotionData that)) return false;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %s",
                getCode(),
                getDiscountPercentage() + "%",
                getName()
        );
    }
}
