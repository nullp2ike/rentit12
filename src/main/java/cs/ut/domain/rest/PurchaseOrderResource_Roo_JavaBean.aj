// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package cs.ut.domain.rest;

import cs.ut.domain.HireRequestStatus;
import cs.ut.domain.rest.PurchaseOrderResource;
import java.math.BigDecimal;
import java.util.Date;

privileged aspect PurchaseOrderResource_Roo_JavaBean {
    
    public Date PurchaseOrderResource.getStartDate() {
        return this.startDate;
    }
    
    public void PurchaseOrderResource.setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date PurchaseOrderResource.getEndDate() {
        return this.endDate;
    }
    
    public void PurchaseOrderResource.setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public BigDecimal PurchaseOrderResource.getTotalCost() {
        return this.totalCost;
    }
    
    public void PurchaseOrderResource.setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    
    public String PurchaseOrderResource.getConstructionSite() {
        return this.constructionSite;
    }
    
    public void PurchaseOrderResource.setConstructionSite(String constructionSite) {
        this.constructionSite = constructionSite;
    }
    
    public String PurchaseOrderResource.getSiteEngineer() {
        return this.siteEngineer;
    }
    
    public void PurchaseOrderResource.setSiteEngineer(String siteEngineer) {
        this.siteEngineer = siteEngineer;
    }
    
    public HireRequestStatus PurchaseOrderResource.getStatus() {
        return this.status;
    }
    
    public void PurchaseOrderResource.setStatus(HireRequestStatus status) {
        this.status = status;
    }
    
    public long PurchaseOrderResource.getPlantId() {
        return this.plantId;
    }
    
    public void PurchaseOrderResource.setPlantId(long plantId) {
        this.plantId = plantId;
    }
    
}
