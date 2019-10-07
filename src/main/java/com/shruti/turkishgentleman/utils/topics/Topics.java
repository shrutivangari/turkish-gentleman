package com.shruti.turkishgentleman.utils.topics;

public enum Topics {

    COMPANIES {
        @Override
        public String toString() {
            return "companies";
        }
    },
    CLIENTS {
        @Override
        public String toString() {
            return "clients";
        }
    },
    STOCK_TRANSACTIONS_TOPIC {
        @Override
        public String toString() {
            return "stocks-transaction-topic";
        }
    };

    public String topicName() {
        return this.toString();
    }

}
