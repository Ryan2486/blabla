package org.security.utils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class ADGroupRetrieval {

    private final Properties ldapProperties;

    public ADGroupRetrieval() throws IOException {
        ldapProperties = new Properties();
        ClassLoader classLoader = getClass().getClassLoader();
          try (InputStream inputStream = classLoader.getResourceAsStream("ldap.properties")) {
            if (inputStream == null) {
                throw new IOException("Unable to find ldap.properties");
            }
            ldapProperties.load(inputStream);
        }
    }   

    public Set<String> getGroupsFromAD(String username, String password) throws Exception {
        Set<String> groups = new HashSet<>();
        DirContext ctx = null;
        NamingEnumeration<SearchResult> results = null;

        try {
            ctx = createLdapContext(username, password);
            String searchFilter = createSearchFilter(username);
            SearchControls searchControls = createSearchControls();

            results = ctx.search(ldapProperties.getProperty("ldap.base.dn"), searchFilter, searchControls);
            if (results.hasMore()) {
                SearchResult searchResult = results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute memberOf = attributes.get("memberOf");
                if (memberOf != null) {
                    for (int i = 0; i < memberOf.size(); i++) {
                        String groupDN = (String) memberOf.get(i);
                        groups = extractGroupNames(groupDN);                    }
                }
            }
            return groups;
        } catch (NamingException e) {
            // Log the error and throw a custom exception
            e.printStackTrace();
            throw new Exception("Failed to retrieve AD groups because "+ e.getMessage());
        } finally {
            closeResources(ctx, results);
        }

    }

    public String getHighestLevelGroupFromAD(String username, String password) throws Exception {
        Set<String> groups = null;
        DirContext ctx = null;
        NamingEnumeration<SearchResult> results = null;
    
        try {
            ctx = createLdapContext(username, password);
            String searchFilter = createSearchFilter(username);
            SearchControls searchControls = createSearchControls();
    
            results = ctx.search(ldapProperties.getProperty("ldap.base.dn"), searchFilter, searchControls);
            if (results.hasMore()) {
                SearchResult searchResult = results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute memberOf = attributes.get("memberOf");
                if (memberOf != null) {
                    for (int i = 0; i < memberOf.size(); i++) {
                        String groupDN = (String) memberOf.get(i);
                        // String groupName = ;
                        groups = extractGroupNames(groupDN);
                    }
                }
            }
            // Now determine the highest level group
            String highestLevelGroup = null;
            int highestLevel = Integer.MAX_VALUE;
    
            for (String groupDN : groups) {
                int level = countGroupHierarchyLevel(groupDN);
                if (level < highestLevel) {
                    highestLevel = level;
                    highestLevelGroup = groupDN;
                }
            }
    
            return highestLevelGroup;
        } catch (NamingException e) {
            // Log the error and throw a custom exception
            e.printStackTrace();
            throw new Exception("Failed to retrieve AD groups because " + e.getMessage());
        } finally {
            closeResources(ctx, results);
        }
    }
    
    // Helper method to count the number of components in the DN to determine hierarchy level
    private int countGroupHierarchyLevel(String groupDN) {
        return groupDN.split(",").length;
    }
    

    public Set<String> extractGroupNames(String groupDN) {
        Set<String> names = new HashSet<>();
        
        // Split the string by commas to get each component of the DN
        String[] components = groupDN.split(",");
        
        // Iterate over each component
        for (String component : components) {
            // Check if the component starts with "CN="
            if (component.trim().startsWith("CN=")) {
                // Extract the name after "CN="
                String name = component.substring(3).trim();
                names.add(name);
            }
        }
        
        return names;
    }

    private DirContext createLdapContext(String username, String password) throws NamingException {
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapProperties.getProperty("ldap.url"));
        env.put(Context.SECURITY_AUTHENTICATION, ldapProperties.getProperty("ldap.security.authentication"));
        env.put(Context.SECURITY_PRINCIPAL, username + ldapProperties.getProperty("ldap.security.principal.suffix"));
        env.put(Context.SECURITY_CREDENTIALS, password);

        // Add connection pooling properties
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        env.put("com.sun.jndi.ldap.connect.pool.timeout", ldapProperties.getProperty("ldap.pool.timeout"));
        env.put("com.sun.jndi.ldap.connect.pool.prefsize", ldapProperties.getProperty("ldap.pool.min.size"));
        env.put("com.sun.jndi.ldap.connect.pool.maxsize", ldapProperties.getProperty("ldap.pool.max.size"));

        return new InitialDirContext(env);
    }

    private String createSearchFilter(String username) {
        // Escape special characters to prevent LDAP injection
        String escapedUsername = escapeLDAPSearchFilter(username);
        String baseFilter = ldapProperties.getProperty("ldap.search.filter");
        return "(&" + baseFilter + "(sAMAccountName=" + escapedUsername + "))";
    }

    
    private void closeResources(DirContext ctx, NamingEnumeration<SearchResult> results) throws Exception{
        if (results != null) {
            try {
                results.close();
            } catch (NamingException e) {
                throw e;
            }
        }
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                throw e;
            }
        }
    }

    private SearchControls createSearchControls() {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setReturningAttributes(ldapProperties.getProperty("ldap.search.attributes").split(","));
        return searchControls;
    }
  // Method to escape special characters in LDAP search filter
  private String escapeLDAPSearchFilter(String filter) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < filter.length(); i++) {
        char c = filter.charAt(i);
        switch (c) {
            case '\\': sb.append("\\5c"); break;
            case '*': sb.append("\\2a"); break;
            case '(': sb.append("\\28"); break;
            case ')': sb.append("\\29"); break;
            case '\u0000': sb.append("\\00"); break;
            default: sb.append(c);
        }
    }
    return sb.toString();
}
}
