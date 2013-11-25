package cs.ut.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class PurchaseOrder {

    /**
     */
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-")
    @NotNull
    private Date startDate;

    /**
     */
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(style = "S-")
    @NotNull
    private Date endDate;

    /**
     */
    @NotNull
    private BigDecimal totalCost;

    /**
     */
    @Enumerated
    @NotNull
    private HireRequestStatus status;

    /**
     */
    @ManyToOne
    @NotNull
    private Plant plant;
    
    @NotNull
    private long plantHireRequestId;
    
    @NotNull
    boolean invoiceSent;
}
