package com.zju.vis.print_backend;

import com.zju.vis.print_backend.Utils.Utils;
import com.zju.vis.print_backend.dao.RawMaterialRepository;
import com.zju.vis.print_backend.entity.RawMaterial;
import com.zju.vis.print_backend.service.FilterCakeService;
import com.zju.vis.print_backend.service.RawMaterialService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.zju.vis.print_backend.Utils.Utils.stepMonth;

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

	@Resource
	RawMaterialService rawMaterialService;

	@Resource
	RawMaterialRepository rawMaterialRepository;

	@Test
	void testRawMaterialHistoryPrice(){
		RawMaterial rawMaterial = rawMaterialRepository.findRawMaterialByRawMaterialId(new Long(10));
		Date date = stepMonth(new Date(),-1);
		Double historyPrice = rawMaterialService.getRawMaterialHistoryPrice(rawMaterial,date);
		System.out.println(date);
		System.out.println(historyPrice);
	}
}
