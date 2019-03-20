package json;

import com.tool.dto.Table;
import com.tool.service.TableService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by XiuYin.Cui on 2018/1/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class Test01 {

    @Autowired
    private TableService tableService;

    @Test
    public void invoke(){
        List<Table> tables = tableService.tableList();
        for (Table table : tables) {
            System.out.println(table);
        }
    }
}
