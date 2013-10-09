package cs.ut.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class PurchaseOrder {

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    @NotNull
    private Date startDate;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    @NotNull
    private Date endDate;

    /**
     */
    @NotNull
    private BigDecimal totalCost;

    /**
     */
    @NotNull
    private String constructionSite;
    
    /**
     */
    @NotNull
    private String siteEngineer;

    /**
     */
    @Enumerated
    @NotNull
    private HireRequestStatus status;

    /**
     */
    @OneToOne
    @NotNull
    private Plant plant;
}
