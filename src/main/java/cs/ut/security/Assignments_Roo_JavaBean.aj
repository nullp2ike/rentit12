// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package cs.ut.security;

import cs.ut.security.Assignments;
import cs.ut.security.Authorities;
import cs.ut.security.Users;

privileged aspect Assignments_Roo_JavaBean {
    
    public Users Assignments.getUserRentit() {
        return this.userRentit;
    }
    
    public void Assignments.setUserRentit(Users userRentit) {
        this.userRentit = userRentit;
    }
    
    public Authorities Assignments.getAuthority() {
        return this.authority;
    }
    
    public void Assignments.setAuthority(Authorities authority) {
        this.authority = authority;
    }
    
}