package cs.ut.domain;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml")
@Transactional
@RooIntegrationTest(entity = PurchaseOrder.class)
public class PurchaseOrderIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    PurchaseOrderDataOnDemand dod;

	@Test
    public void testCountPurchaseOrders() {
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", dod.getRandomPurchaseOrder());
        long count = PurchaseOrder.countPurchaseOrders();
        Assert.assertTrue("Counter for 'PurchaseOrder' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindPurchaseOrder() {
        PurchaseOrder obj = dod.getRandomPurchaseOrder();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to provide an identifier", id);
        obj = PurchaseOrder.findPurchaseOrder(id);
        Assert.assertNotNull("Find method for 'PurchaseOrder' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'PurchaseOrder' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllPurchaseOrders() {
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", dod.getRandomPurchaseOrder());
        long count = PurchaseOrder.countPurchaseOrders();
        Assert.assertTrue("Too expensive to perform a find all test for 'PurchaseOrder', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<PurchaseOrder> result = PurchaseOrder.findAllPurchaseOrders();
        Assert.assertNotNull("Find all method for 'PurchaseOrder' illegally returned null", result);
        Assert.assertTrue("Find all method for 'PurchaseOrder' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindPurchaseOrderEntries() {
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", dod.getRandomPurchaseOrder());
        long count = PurchaseOrder.countPurchaseOrders();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<PurchaseOrder> result = PurchaseOrder.findPurchaseOrderEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'PurchaseOrder' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'PurchaseOrder' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        PurchaseOrder obj = dod.getRandomPurchaseOrder();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to provide an identifier", id);
        obj = PurchaseOrder.findPurchaseOrder(id);
        Assert.assertNotNull("Find method for 'PurchaseOrder' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyPurchaseOrder(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'PurchaseOrder' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        PurchaseOrder obj = dod.getRandomPurchaseOrder();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to provide an identifier", id);
        obj = PurchaseOrder.findPurchaseOrder(id);
        boolean modified =  dod.modifyPurchaseOrder(obj);
        Integer currentVersion = obj.getVersion();
        PurchaseOrder merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'PurchaseOrder' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", dod.getRandomPurchaseOrder());
        PurchaseOrder obj = dod.getNewTransientPurchaseOrder(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'PurchaseOrder' identifier to be null", obj.getId());
        try {
            obj.persist();
        } catch (final ConstraintViolationException e) {
            final StringBuilder msg = new StringBuilder();
            for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                final ConstraintViolation<?> cv = iter.next();
                msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
            }
            throw new IllegalStateException(msg.toString(), e);
        }
        obj.flush();
        Assert.assertNotNull("Expected 'PurchaseOrder' identifier to no longer be null", obj.getId());
    }

	@Test
    public void testRemove() {
        PurchaseOrder obj = dod.getRandomPurchaseOrder();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'PurchaseOrder' failed to provide an identifier", id);
        obj = PurchaseOrder.findPurchaseOrder(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'PurchaseOrder' with identifier '" + id + "'", PurchaseOrder.findPurchaseOrder(id));
    }
}
