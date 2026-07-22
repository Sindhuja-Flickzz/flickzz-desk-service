package com.flickzz.desk.mapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.flickzz.desk.model.BPCategory;
import com.flickzz.desk.model.BPConfiguration;
import com.flickzz.desk.model.BPSubCategory;
import com.flickzz.desk.model.BPSupportGroup;
import com.flickzz.desk.vo.BPCategoryVO;
import com.flickzz.desk.vo.BPSupportGroupVO;

class CommonMapperTest {

	private final CommonMapper mapper = new CommonMapper();

	@Test
	void shouldMapSupportGroupToVo() {
		BPConfiguration configuration = new BPConfiguration();
		configuration.setConfigurationId(11L);

		BPSupportGroup supportGroup = BPSupportGroup.builder().supportGroupId(22L).configuration(configuration)
				.groupName("Billing").isActive(Boolean.TRUE).createdBy(1L).updatedBy(2L).build();

		BPSupportGroupVO vo = mapper.toSupportGroupVo(supportGroup);

		assertNotNull(vo);
		assertEquals(22L, vo.getSupportGroupId());
		assertEquals(11L, vo.getConfiguration().getConfigurationId());
		assertEquals("Billing", vo.getGroupName());
		assertTrue(vo.getIsActive());
		assertEquals(1L, vo.getCreatedBy());
		assertEquals(2L, vo.getUpdatedBy());
	}

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
