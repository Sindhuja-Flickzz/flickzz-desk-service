package com.flickzz.desk.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.flickzz.desk.model.BPCategory;
import com.flickzz.desk.model.BPConfiguration;
import com.flickzz.desk.model.BPSubCategory;
import com.flickzz.desk.vo.BPCategoryVO;

class CommonMapperTest {

	private final CommonMapper mapper = new CommonMapper();

	@Test
	void shouldMapCategoryToVo() {
		BPConfiguration configuration = new BPConfiguration();
		configuration.setConfigurationId(10L);

		BPSubCategory subCategory = BPSubCategory.builder()
				.subCategoryName("Billing")
				.isActive(Boolean.TRUE)
				.createdBy(3L)
				.updatedBy(4L)
				.build();

		BPCategory category = BPCategory.builder()
				.configuration(configuration)
				.categoryName("Support")
				.isActive(Boolean.TRUE)
				.createdBy(1L)
				.updatedBy(2L)
				.subCategories(List.of(subCategory))
				.build();

		BPCategoryVO vo = mapper.toBPCategoryVo(category);

		assertNotNull(vo);
		assertEquals(10L, vo.getConfiguration().getConfigurationId());
		assertEquals("Support", vo.getCategoryName());
		assertEquals(Boolean.TRUE, vo.getIsActive());
		assertEquals(1L, vo.getCreatedBy());
		assertEquals(2L, vo.getUpdatedBy());
		assertNotNull(vo.getSubCategories());
		assertEquals(1, vo.getSubCategories().size());
		assertEquals("Billing", vo.getSubCategories().get(0).getSubCategoryName());
	}
}
