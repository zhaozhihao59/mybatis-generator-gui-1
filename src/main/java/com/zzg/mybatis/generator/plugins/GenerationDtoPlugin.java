package com.zzg.mybatis.generator.plugins;

import static org.mybatis.generator.internal.util.JavaBeansUtil.getJavaBeansField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.Plugin;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

public class GenerationDtoPlugin extends PluginAdapter {

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> ans = new ArrayList<GeneratedJavaFile>();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(properties.getProperty("dtoType"));
		TopLevelClass topLevelClass = new TopLevelClass(type);
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		topLevelClass.addAnnotation("@Builder");
		topLevelClass.addAnnotation("@Data");
		topLevelClass.addAnnotation("@NoArgsConstructor");
		topLevelClass.addAnnotation("@AllArgsConstructor");
		// add importedType
		topLevelClass.addImportedType("lombok.AllArgsConstructor");
		topLevelClass.addImportedType("lombok.Builder");
		topLevelClass.addImportedType("lombok.Data");
		topLevelClass.addImportedType("lombok.NoArgsConstructor");

		List<IntrospectedColumn> introspectedColumns = getColumnsInThisClass(introspectedTable);
		
		for (IntrospectedColumn introspectedColumn : introspectedColumns) {
			
			Field field = getJavaBeansField(introspectedColumn, context, introspectedTable);
            topLevelClass.addField(field);
            topLevelClass.addImportedType(field.getType());
		}
		
		GeneratedJavaFile dtoFile = new GeneratedJavaFile(topLevelClass, properties.getProperty("dtoTargePackage"),
				introspectedTable.getContext().getJavaFormatter());;
		if (context.getPlugins().modelBaseRecordClassGenerated(
                topLevelClass, introspectedTable)) {
			dtoFile = new GeneratedJavaFile(topLevelClass, properties.getProperty("dtoTargePackage"),
					introspectedTable.getContext().getJavaFormatter());
        }
		ans.add(dtoFile);
		return ans;
	}


	private boolean includePrimaryKeyColumns(IntrospectedTable introspectedTable) {
		return !introspectedTable.getRules().generatePrimaryKeyClass() && introspectedTable.hasPrimaryKeyColumns();
	}

	private boolean includeBLOBColumns(IntrospectedTable introspectedTable) {
		return !introspectedTable.getRules().generateRecordWithBLOBsClass() && introspectedTable.hasBLOBColumns();
	}

	private List<IntrospectedColumn> getColumnsInThisClass(IntrospectedTable introspectedTable) {
		List<IntrospectedColumn> introspectedColumns;
		if (includePrimaryKeyColumns(introspectedTable)) {
			if (includeBLOBColumns(introspectedTable)) {
				introspectedColumns = introspectedTable.getAllColumns();
			} else {
				introspectedColumns = introspectedTable.getNonBLOBColumns();
			}
		} else {
			if (includeBLOBColumns(introspectedTable)) {
				introspectedColumns = introspectedTable.getNonPrimaryKeyColumns();
			} else {
				introspectedColumns = introspectedTable.getBaseColumns();
			}
		}

		return introspectedColumns;
	}

}
