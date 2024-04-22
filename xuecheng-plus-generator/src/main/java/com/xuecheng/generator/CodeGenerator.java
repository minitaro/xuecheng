package com.xuecheng.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;

/**
* @description 代码生成器
* @author TMC
* @date 2023/3/1 21:59
* @version 1.0
*/
public class CodeGenerator {

    private static final String Global_SERVICE_NAME = "%sService"; //service接口命名模板
    private static final String Global_SERVICEIMPL_NAME = "%sServiceImpl"; //service接口实现类命名模板
    private static final String Global_MAPPER_NAME = "%sMapper"; //mapper接口命名模板 xxMapper
    private static final String Global_XML_NAME = "%sMapper"; //xml命名模板 xxMapper.xml
    private static final String Global_ENTITY_NAME = "%sDto"; //实体类命名模板 xxD
    private static final String Package_XML = "mapper"; //xml文件目录名
    private static final String Package_SERVICE = "service"; //xml文件目录名
    private static final String Package_SERVICE_IMPL = "service.impl"; //服务实现类目录名

    //TODO 按需修改参数
    private static final String Global_OUTPUT_DIR = "/xuecheng-plus-generator/src/main/java"; //生成代码文件输出目录
    private static final String Package_PARENT = "com.xuecheng"; //包名
    private static final String Package_MODULE_NAME = "content"; //TODO 模块名
    private static final String Package_ENTITY = "model.po"; //TODO 数据模型目录名,生成DTO时改为model.dto
    private static final boolean Global_IS_DTO = false; // TODO 默认生成entity,需要生成DTO修改此变量
    private static final String Global_AUTHOR = "TMC"; //作者
    private static final String DataSource_USERNAME = "root"; //数据库用户名
    private static final String DataSource_PASSWORD  = "mysql"; //数据库密码
    private static final String DataSource_URL  = "jdbc:mysql://192.168.101.65:3306/"; //数据库服务寻址
    private static final String DataSource_DB_NAME  = "xc0_content"; //TODO 数据库名称
    private static final String DataSource_URL_PARAM  = "?serverTimezone=UTC&useUnicode=true&useSSL=false" +
            "&characterEncoding=utf8"; //URL请求参数
    private static final String DataSource_DRIVER_NAME  = "com.mysql.cj.jdbc.Driver"; //数据库驱动 "com.mysql.jdbc.Driver"

    //TODO 数据库表名
    private static final String[] TABLE_NAMES = new String[]{
            "mq_message",
            "mq_message_history",
            "course_base",
            "course_market",
            "course_teacher",
            "course_publish",
            "course_publish_pre",
            "course_category",
            "teachplan",
            "teachplan_media"
    }; //数据库表名

    //TODO 需要自动填充的字段
    @AllArgsConstructor
    @Getter
    private enum TableFiller {

        CREATE_DATE("create_date", FieldFill.INSERT),
        CHANGE_DATE("change_date", FieldFill.INSERT_UPDATE),
        MODIFY_DATE("modify_date", FieldFill.UPDATE),
        ;

        private String fieldName; //字段名
        private FieldFill fieldFill; //自动填充策略

    }


    public static void main(String[] args) {

        //1.全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(System.getProperty("user.dir") + Global_OUTPUT_DIR); //代码生成路径
        globalConfig.setAuthor(Global_AUTHOR); //作者
        globalConfig.setFileOverride(true); //是否覆盖已有文件
        globalConfig.setOpen(false); // 是否打开输出目录
        globalConfig.setSwagger2(false);
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        globalConfig.setServiceName(Global_SERVICE_NAME);
        globalConfig.setServiceImplName(Global_SERVICEIMPL_NAME);
        globalConfig.setMapperName(Global_MAPPER_NAME);
        globalConfig.setXmlName(Global_XML_NAME);
        if (Global_IS_DTO) {
            globalConfig.setSwagger2(true);
            globalConfig.setEntityName(Global_ENTITY_NAME);
        }

        //2.包配置
        PackageConfig packageInfo = new PackageConfig();
        packageInfo.setParent(Package_PARENT);
        packageInfo.setModuleName(Package_MODULE_NAME);
        packageInfo.setService(Package_SERVICE);
        packageInfo.setServiceImpl(Package_SERVICE_IMPL);
        packageInfo.setXml(Package_XML);
        packageInfo.setEntity(Package_ENTITY);

        //3.数据库配置
        DataSourceConfig dataSource = new DataSourceConfig();
        dataSource.setDbType(DbType.MYSQL);
        dataSource.setUrl(DataSource_URL + DataSource_DB_NAME + DataSource_URL_PARAM);
        dataSource.setDriverName(DataSource_DRIVER_NAME);
        dataSource.setUsername(DataSource_USERNAME);
        dataSource.setPassword(DataSource_PASSWORD);

        //4.策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel); //表名生成策略:下划线_驼峰映射
        strategy.setColumnNaming(NamingStrategy.underline_to_camel); //表字段映射到实体的命名策略
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setInclude(TABLE_NAMES);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix(packageInfo.getModuleName() + "_");
        strategy.setEntityBooleanColumnRemoveIsPrefix(true); // Boolean类型字段是否移除is前缀处理
        strategy.setRestControllerStyle(true);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        Arrays.stream(TableFiller.values()).forEach(item -> {
            tableFills.add(new TableFill(item.getFieldName(), item.getFieldFill()));
        });
        strategy.setTableFillList(tableFills); // 自动填充字段配置

        //5.设置模板
        TemplateConfig template = new TemplateConfig();

        //6.设置模板引擎:freemarker引擎,默认Velocity
        FreemarkerTemplateEngine templateEngine = new FreemarkerTemplateEngine();

        //7.配置代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(globalConfig);
        autoGenerator.setPackageInfo(packageInfo);
        autoGenerator.setDataSource(dataSource);
        autoGenerator.setStrategy(strategy);
        autoGenerator.setTemplate(template);
        autoGenerator.setTemplateEngine(templateEngine);

        //8.生成代码
        autoGenerator.execute();

    }

}

