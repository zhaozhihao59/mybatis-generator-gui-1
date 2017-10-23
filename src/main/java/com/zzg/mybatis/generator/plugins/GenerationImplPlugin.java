package com.zzg.mybatis.generator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class GenerationImplPlugin extends PluginAdapter{

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
	
	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> ans = new ArrayList<GeneratedJavaFile>();
		
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(properties.getProperty("implType"));
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.addAnnotation("@Slf4j");
		topLevelClass.addAnnotation("@Service(" + type.getShortName().substring(0, 1).toLowerCase() + type.getShortName().substring(1) + ")");
		topLevelClass.addAnnotation("@Transactional(\"transactionManager\")");
		return super.contextGenerateAdditionalJavaFiles(introspectedTable);
	}

}
