package com.shruti.turkishgentleman.utils.topics;

public enum Topics {

    TRANSACTIONS {
        @Override
        public String toString() {
            return "transactions";
        }
    },
    TRANSACTIONS_TRANSFORMED {
      @Override
      public String toString() {
          return "transactions-transformer";
      }
    },
    COMPANIES{
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
    STREAMS_DEMO{
      @Override
      public String toString() {
          return "streams-demo";
      }
    },
    STREAMS_JOINS{
      @Override
      public String toString() {
          return "streams-joins";
      }
    },
    STREAMS_STATEFUL {
      @Override
      public String toString() {
          return "streams-stateful";
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
