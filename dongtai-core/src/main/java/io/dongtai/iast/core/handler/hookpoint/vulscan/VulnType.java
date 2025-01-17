package io.dongtai.iast.core.handler.hookpoint.vulscan;

/**
 * @author dongzhiyong@huoxian.cn
 */

public enum VulnType {

    /**
     * 漏洞
     */
    SQL_OVER_POWER("sql-over-power", "info", false),
    CRYPTO_WEAK_RANDOMNESS("crypto-weak-randomness", "low", false),
    CRYPTO_BAC_CIPHERS("crypto-bad-ciphers", "high", false),
    CRYPTO_BAD_MAC("crypto-bad-mac", "high", false),
    COOKIE_FLAGS_MISSING("cookie-flags-missing", "high", true),
    REFLECTED_XSS("reflected-xss", "medium", true),
    SQL_INJECTION("sql-injection", "high", true),
    HQL_INJECTION("hql-injection", "high", true),
    LDAP_INJECTION("ldap-injection", "high", true),
    CMD_INJECTION("cmd-injection", "high", true),
    XPATH_INJECTION("xpath-injection", "high", true),
    PATH_TRAVERSAL("path-traversal", "high", true),
    XXE("xxe", "medium", true),
    UNVALIDATED_REDIRECT("unvalidated-redirect", "low", true),
    ;

    public String getName() {
        return name;
    }

    /**
     * 漏洞类型 值
     */
    String name;
    String weight;
    boolean tracked;

    VulnType(String name, String weight, boolean tracked) {
        this.name = name;
        this.weight = weight;
        this.tracked = tracked;
    }


    public boolean equals(String name) {
        return this.name.equals(name);
    }

    public static VulnType getTypeByName(String name) {
        for (VulnType vType : VulnType.values()) {
            if (vType.equals(name)) {
                return vType;
            }
        }
        return null;
    }
}
