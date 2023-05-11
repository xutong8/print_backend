package com.zju.vis.print_backend;

import com.zju.vis.print_backend.Utils.Utils;
import com.zju.vis.print_backend.service.FilterCakeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class PrintBackendApplicationTests {

	@Test
	void contextLoads() {
	}

	@Resource
	FilterCakeService filterCakeService;


	@Test
	void testHistoryList(){
		// List<Utils.HistoryPrice> list = filterCakeService.getFilterCakeHistoryPriceList();
	}

}
