package org.finance.financemanager.accessibility.roles.entities;

public enum RoleName {
    CLIENT,
    ADMIN;

    public boolean client() {return this.equals(CLIENT);}
    public boolean admin() {return this.equals(ADMIN);}
}
