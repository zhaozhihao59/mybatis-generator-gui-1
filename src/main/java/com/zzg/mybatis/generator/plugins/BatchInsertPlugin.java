package com.zzg.mybatis.generator.plugins;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

public class BatchInsertPlugin extends PluginAdapter{

	@Override
	public boolean validate(List<String> warnings) {
		return true;
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		// 设置需要导入的类
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		importedTypes.add(FullyQualifiedJavaType.getNewListInstance());
		importedTypes.add(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()));

		Method ibsmethod = new Method();
		// 1.设置方法可见性
		ibsmethod.setVisibility(JavaVisibility.PUBLIC);
		// 2.设置返回值类型
		FullyQualifiedJavaType ibsreturnType = FullyQualifiedJavaType.getIntInstance();// int型
		ibsmethod.setReturnType(ibsreturnType);
		// 3.设置方法名
		ibsmethod.setName("insertBatch");
		// 4.设置参数列表
		FullyQualifiedJavaType paramType = FullyQualifiedJavaType.getNewListInstance();
		FullyQualifiedJavaType paramListType;
		if (introspectedTable.getRules().generateBaseRecordClass()) {
			paramListType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		}
		else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
			paramListType = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
		}
		else {
			throw new RuntimeException(getString("RuntimeError.12")); //$NON-NLS-1$  
		}
		paramType.addTypeArgument(paramListType);

		ibsmethod.addParameter(new Parameter(paramType, "records"));

		interfaze.addImportedTypes(importedTypes);
		interfaze.addMethod(ibsmethod);
		return super.clientGenerated(interfaze,topLevelClass,introspectedTable);
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		List<IntrospectedColumn> columns = introspectedTable.getAllColumns();
        //获得要自增的列名
        String incrementField = introspectedTable.getTableConfiguration().getProperties().getProperty("incrementField");
        if (incrementField != null) {
            incrementField = incrementField.toUpperCase();
        }
        StringBuilder dbcolumnsName = new StringBuilder();
        StringBuilder javaPropertyAndDbType = new StringBuilder();
        for (IntrospectedColumn introspectedColumn : columns) {
            String columnName = introspectedColumn.getActualColumnName();
            
            if (!columnName.toUpperCase().equals("ID")) {//不是自增字段的才会出现在批量插入中
                dbcolumnsName.append(columnName + ",");
                javaPropertyAndDbType.append("#{item." + introspectedColumn.getJavaProperty() + ",jdbcType=" + introspectedColumn.getJdbcTypeName() + "},");
            }
        }

        XmlElement insertBatchElement = new XmlElement("insert");
        insertBatchElement.addAttribute(new Attribute("id", "insertBatch"));
        insertBatchElement.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
        insertBatchElement.addElement(new TextElement("insert into " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()));

        XmlElement trim1Element = new XmlElement("trim");
        trim1Element.addAttribute(new Attribute("prefix", "("));
        trim1Element.addAttribute(new Attribute("suffix", ")"));
        trim1Element.addAttribute(new Attribute("suffixOverrides", ","));
        trim1Element.addElement(new TextElement(dbcolumnsName.toString()));
        insertBatchElement.addElement(trim1Element);

        insertBatchElement.addElement(new TextElement("values"));

        XmlElement foreachElement = new XmlElement("foreach");
        foreachElement.addAttribute(new Attribute("collection", "list"));
        foreachElement.addAttribute(new Attribute("index", "index"));
        foreachElement.addAttribute(new Attribute("item", "item"));
        foreachElement.addAttribute(new Attribute("separator", ","));
        foreachElement.addElement(new TextElement("("));
        XmlElement trim2Element = new XmlElement("trim");
        trim2Element.addAttribute(new Attribute("suffixOverrides", ","));
        trim2Element.addElement(new TextElement(javaPropertyAndDbType.toString()));
        foreachElement.addElement(trim2Element);
        foreachElement.addElement(new TextElement(")"));
        insertBatchElement.addElement(foreachElement);

        document.getRootElement().addElement(insertBatchElement);
		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}
	
}
