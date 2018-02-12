import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import application.data.EmployeeObject;
import application.factory.EmployeeObjectFactory;
import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestEmployeePool {

  GenericObjectPool<EmployeeObject> employeeObjectPool;

  @Before
  public void setUp() {
    employeeObjectPool = new GenericObjectPool<>(new EmployeeObjectFactory());
  }

  @After
  public void tearDownPool() throws Exception {
    final String poolName = employeeObjectPool.getJmxName().toString();
    employeeObjectPool.close();
    employeeObjectPool.clear();
    employeeObjectPool = null;
    final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    final Set<ObjectName> result = mbs.queryNames(new ObjectName(
        "apache.pool2:type=GenericObjectPool,*"), null);
    final int registeredPoolCount = result.size();
    final StringBuilder poolInfo = new StringBuilder("Current pool is: ");
    poolInfo.append(poolName);
    poolInfo.append("  Still open pools are: ");
    for (final ObjectName name : result) {
      poolInfo.append(name.toString());
      poolInfo.append(" created via\n");
      poolInfo.append(mbs.getAttribute(name, "CreationStackTrace"));
      poolInfo.append('\n');
      mbs.unregisterMBean(name);
    }
    assertEquals(0, registeredPoolCount);
    Assert.assertEquals(poolInfo.toString(), 0, registeredPoolCount);
  }

  @Test(timeout = 10000)
  public void testPoolCapacity() {
    employeeObjectPool.setTestOnBorrow(true);
    employeeObjectPool.setMaxTotal(40);
    try {
      for (int i = 0; i < 40; i++) {
        employeeObjectPool.addObject();
      }
    } catch (Exception ie) {
      ie.printStackTrace();
    }
    assertEquals(employeeObjectPool.getNumIdle(), 40);
  }

  @Test(timeout = 10000)
  public void testWhenPoolExhausted() throws Exception {
    employeeObjectPool.setMaxTotal(1);
    EmployeeObject poolObject = employeeObjectPool.borrowObject();
    assertNotNull(poolObject);
    Thread.sleep(3000);
    employeeObjectPool.returnObject(poolObject);
    employeeObjectPool.borrowObject();
    employeeObjectPool.returnObject(poolObject);
    assertEquals(1, employeeObjectPool.getNumIdle());
  }

  @Test
  public void testPoolOnReturn(){
    employeeObjectPool.setMaxTotal(1);
    EmployeeObject rogueObject = new EmployeeObject.Builder().build();
    employeeObjectPool.returnObject(rogueObject);
    employeeObjectPool.setTestOnReturn(true);
  }

  @Test
  public void testValidationOnCreate() throws Exception{
    employeeObjectPool.setMaxTotal(2);
    employeeObjectPool.setTestOnCreate(true);
    EmployeeObject employeeObject = employeeObjectPool.borrowObject();
    employeeObjectPool.returnObject(employeeObject);
  }

  @Test
  public void testValidationOnBorrow() throws Exception{
    employeeObjectPool.setMaxTotal(1);
    employeeObjectPool.setTestOnBorrow(true);
    EmployeeObject employeeObject = employeeObjectPool.borrowObject();
    employeeObjectPool.returnObject(employeeObject);
  }
}
