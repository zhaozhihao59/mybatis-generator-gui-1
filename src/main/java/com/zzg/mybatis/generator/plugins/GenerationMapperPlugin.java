package com.zzg.mybatis.generator.plugins;

import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class GenerationMapperPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        List<GeneratedJavaFile> ans = new ArrayList<>();
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(properties.getProperty("daoType"));
        Interface anInterface = new Interface(type);
        anInterface.setVisibility(JavaVisibility.PUBLIC);
        FullyQualifiedJavaType basicType = new FullyQualifiedJavaType(properties.getProperty("superType"));
        anInterface.addSuperInterface(new FullyQualifiedJavaType(basicType.getShortName()));
        anInterface.addImportedType(new FullyQualifiedJavaType(basicType.getFullyQualifiedName()));
        GeneratedJavaFile daoFile = new GeneratedJavaFile(anInterface,
                properties.getProperty("daoTargePackage"),
                introspectedTable.getContext().getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                introspectedTable.getContext().getJavaFormatter());
        daoFile.setRewrited(false);
        ans.add(daoFile);
        return ans;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> ans = new ArrayList<>();
        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        FullyQualifiedJavaType type = new FullyQualifiedJavaType(properties.getProperty("daoType"));
        document.setRootElement(getRootElement(type.getFullyQualifiedName()));

        GeneratedXmlFile generatedXmlFile = new GeneratedXmlFile(document,type.getShortName() + ".xml",
                properties.getProperty("mapperTargetPackage"),
                properties.getProperty("targetProject"),
                introspectedTable.getContext().isMergeable(),
                introspectedTable.getContext().getXmlFormatter());
        generatedXmlFile.setRewrited(false);
        ans.add(generatedXmlFile);
        return ans;
    }

    private XmlElement getRootElement(String fullName){
        XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
        String namespace = fullName;
        answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
                namespace));
        return answer;
    }
}
