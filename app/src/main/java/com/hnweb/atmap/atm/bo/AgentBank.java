package com.hnweb.atmap.atm.bo;

public class AgentBank {

    String id,agent_id,agent_bank_name,agent_acc_num,agent_ssn,agent_router_number,stripe_acc_id,default_account,created_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgent_id() {
        return agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    public String getAgent_bank_name() {
        return agent_bank_name;
    }

    public void setAgent_bank_name(String agent_bank_name) {
        this.agent_bank_name = agent_bank_name;
    }

    public String getAgent_acc_num() {
        return agent_acc_num;
    }

    public void setAgent_acc_num(String agent_acc_num) {
        this.agent_acc_num = agent_acc_num;
    }

    public String getAgent_ssn() {
        return agent_ssn;
    }

    public void setAgent_ssn(String agent_ssn) {
        this.agent_ssn = agent_ssn;
    }

    public String getAgent_router_number() {
        return agent_router_number;
    }

    public void setAgent_router_number(String agent_router_number) {
        this.agent_router_number = agent_router_number;
    }

    public String getStripe_acc_id() {
        return stripe_acc_id;
    }

    public void setStripe_acc_id(String stripe_acc_id) {
        this.stripe_acc_id = stripe_acc_id;
    }

    public String getDefault_account() {
        return default_account;
    }

    public void setDefault_account(String default_account) {
        this.default_account = default_account;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
