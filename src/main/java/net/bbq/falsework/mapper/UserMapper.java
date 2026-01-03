package net.bbq.falsework.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.bbq.falsework.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM t_user WHERE username = #{username}")
    Optional<User> findByUsername(@Param("username") String username);

    @Select("SELECT * FROM t_user WHERE email = #{email}")
    Optional<User> findByEmail(@Param("email") String email);
}
