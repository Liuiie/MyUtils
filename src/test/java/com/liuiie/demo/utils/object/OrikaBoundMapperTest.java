package com.liuiie.demo.utils.object;

import com.liuiie.demo.utils.object.template.MemberInfoDTO;
import com.liuiie.demo.utils.object.template.MemberInfoVO;
import com.liuiie.demo.utils.object.template.PersonDesInfo;
import com.liuiie.demo.utils.object.template.PersonSrcInfo;
import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * orika实体绑定映射
 *
 * @author Liuiie
 * @since 2024/12/27 16:12
 */
public class OrikaBoundMapperTest {

    @Test
    void testBasicMap() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        BoundMapperFacade<MemberInfoDTO, MemberInfoVO> facade =
                mapperFactory.getMapperFacade(MemberInfoDTO.class, MemberInfoVO.class);

        MemberInfoDTO dto = initMemberInfo();
        MemberInfoVO vo = facade.map(dto);
//        facade.map(dto, vo);
        assertMemberInfoEquals(dto, vo);
    }

    @Test
    void testBasicReverseMap() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        BoundMapperFacade<MemberInfoVO, MemberInfoDTO> facade =
                mapperFactory.getMapperFacade(MemberInfoVO.class, MemberInfoDTO.class);

        MemberInfoDTO dto = initMemberInfo();
        MemberInfoVO vo = facade.mapReverse(dto);
//        facade.mapReverse(dto, vo);
        assertMemberInfoEquals(dto, vo);
    }

    private MemberInfoDTO initMemberInfo() {
        MemberInfoDTO member = new MemberInfoDTO();
        member.setId(1000100010001L);
        member.setName("Gogo");
        member.setAge(14);
        member.setEmail("gogo@oo.com");
        member.setIdentity("employee");
        member.setGroup("marketing");
        return member;
    }

    private void assertMemberInfoEquals(MemberInfoDTO dto, MemberInfoVO vo) {
        assertEquals(dto.getId(), vo.getId());
        assertEquals(dto.getName(), vo.getName());
        assertEquals(dto.getAge(), vo.getAge());
        assertEquals(dto.getGroup(), vo.getGroup());
        assertEquals(dto.getEmail(), vo.getEmail());
        assertEquals(dto.getIdentity(), vo.getIdentity());
    }

    @Test
    void testMapDiffFieldName() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        // 使用注解显式获取变量名，需要指定所有的字段映射关系，剩余通过 byDefault() 指定
        mapperFactory.classMap(PersonSrcInfo.class, PersonDesInfo.class)
                .field(PersonSrcInfo.Fields.name, PersonDesInfo.Fields.personName)
                .field(PersonSrcInfo.Fields.birth, PersonDesInfo.Fields.dateOfBirth)
                .field(PersonSrcInfo.Fields.email, PersonDesInfo.Fields.emailAddress)
                .byDefault()
                .register();

        BoundMapperFacade<PersonSrcInfo, PersonDesInfo> facade =
                mapperFactory.getMapperFacade(PersonSrcInfo.class, PersonDesInfo.class);

        PersonSrcInfo src = initPersonInfo();
        PersonDesInfo dest = facade.map(src);
        assertPersonInfoEquals(src, dest);
    }

    @Test
    void testReverseMapDiffFieldName() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        // 使用注解显式获取变量名，需要指定所有的字段映射关系，剩余通过 byDefault() 指定
        mapperFactory.classMap(PersonSrcInfo.class, PersonDesInfo.class)
                .field(PersonSrcInfo.Fields.name, PersonDesInfo.Fields.personName)
                .field(PersonSrcInfo.Fields.birth, PersonDesInfo.Fields.dateOfBirth)
                .field(PersonSrcInfo.Fields.email, PersonDesInfo.Fields.emailAddress)
                .byDefault()
                .register();

        BoundMapperFacade<PersonDesInfo, PersonSrcInfo> facade =
                mapperFactory.getMapperFacade(PersonDesInfo.class, PersonSrcInfo.class);

        PersonSrcInfo src = initPersonInfo();
        PersonDesInfo dest = facade.mapReverse(src);
        assertPersonInfoEquals(src, dest);
    }

    private PersonSrcInfo initPersonInfo() {
        PersonSrcInfo person = new PersonSrcInfo();
        person.setId(1000100010001L);
        person.setName("Gogo");
        person.setAge(14);
        person.setBirth(LocalDate.of(2004, 6, 19));
        person.setEmail("gogo@oo.com");
        return person;
    }

    private void assertPersonInfoEquals(PersonSrcInfo src, PersonDesInfo dest) {
        assertEquals(src.getId(), dest.getId());
        assertEquals(src.getName(), dest.getPersonName());
        assertEquals(src.getAge(), dest.getAge());
        assertEquals(src.getEmail(), dest.getEmailAddress());
        assertEquals(src.getBirth(), dest.getDateOfBirth());
    }
}

