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

@ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml")
@Transactional
@RooIntegrationTest(entity = Plant.class)
public class PlantIntegrationTest {

    @Test
    public void testMarkerMethod() {
    }

	@Autowired
    PlantDataOnDemand dod;

	@Test
    public void testCountPlants() {
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", dod.getRandomPlant());
        long count = Plant.countPlants();
        Assert.assertTrue("Counter for 'Plant' incorrectly reported there were no entries", count > 0);
    }

	@Test
    public void testFindPlant() {
        Plant obj = dod.getRandomPlant();
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Plant' failed to provide an identifier", id);
        obj = Plant.findPlant(id);
        Assert.assertNotNull("Find method for 'Plant' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Plant' returned the incorrect identifier", id, obj.getId());
    }

	@Test
    public void testFindAllPlants() {
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", dod.getRandomPlant());
        long count = Plant.countPlants();
        Assert.assertTrue("Too expensive to perform a find all test for 'Plant', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Plant> result = Plant.findAllPlants();
        Assert.assertNotNull("Find all method for 'Plant' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Plant' failed to return any data", result.size() > 0);
    }

	@Test
    public void testFindPlantEntries() {
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", dod.getRandomPlant());
        long count = Plant.countPlants();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Plant> result = Plant.findPlantEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Plant' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Plant' returned an incorrect number of entries", count, result.size());
    }

	@Test
    public void testFlush() {
        Plant obj = dod.getRandomPlant();
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Plant' failed to provide an identifier", id);
        obj = Plant.findPlant(id);
        Assert.assertNotNull("Find method for 'Plant' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyPlant(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Plant' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testMergeUpdate() {
        Plant obj = dod.getRandomPlant();
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Plant' failed to provide an identifier", id);
        obj = Plant.findPlant(id);
        boolean modified =  dod.modifyPlant(obj);
        Integer currentVersion = obj.getVersion();
        Plant merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Plant' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }

	@Test
    public void testPersist() {
        Assert.assertNotNull("Data on demand for 'Plant' failed to initialize correctly", dod.getRandomPlant());
        Plant obj = dod.getNewTransientPlant(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Plant' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Plant' identifier to be null", obj.getId());
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
        Assert.assertNotNull("Expected 'Plant' identifier to no longer be null", obj.getId());
    }
}
