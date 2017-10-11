package com.zzg.mybatis.generator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;


public class GenerationDtoPlugin extends PluginAdapter{

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}
	
	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> ans = new ArrayList<GeneratedJavaFile>();
		
		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
                introspectedTable.getBaseRecordType());
		TopLevelClass topLevelClass = new TopLevelClass(type);
		
		GeneratedJavaFile dtoFile = new GeneratedJavaFile(topLevelClass, "com.haoge.dto", introspectedTable.getContext().getJavaFormatter());
		
		ans.add(dtoFile);
		return super.contextGenerateAdditionalJavaFiles(introspectedTable);
	}
	
	

}
