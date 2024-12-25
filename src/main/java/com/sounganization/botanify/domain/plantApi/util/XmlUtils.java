package com.sounganization.botanify.domain.plantApi.util;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.plantApi.dto.res.PlantApiResDto;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlUtils {

    public static Document parseXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xml.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ExceptionStatus.PARSER_FAILED);
        }
    }

    public static String getTagValue(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    public static String getTagValue(Node node, String tagName) {
        if (node == null || !node.hasChildNodes()) {
            return null;
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals(tagName)) {
                return child.getTextContent();
            }
        }
        return null;
    }

    public static List<String> extractCodesAndNames(NodeList items) {
        List<String> codeAndNames = new ArrayList<>();
        for (int i = 0; i < items.getLength(); i++) {
            String code = getTagValue(items.item(i), "code");
            String codeNm = getTagValue(items.item(i), "codeNm");
            codeAndNames.add(code + ":" + codeNm);
        }
        return codeAndNames;
    }

    public static PlantApiResDto parseSpeciesDetail(Document detailDocument, String codeNm, String cntntsNo, String cntntsSj) {
        String smell = getTagValue(detailDocument, "smellCodeNm");
        String toxicity = getTagValue(detailDocument, "toxctyInfo");
        String managementLevel = getTagValue(detailDocument, "managelevelCodeNm");
        String growthSpeed = getTagValue(detailDocument, "grwtveCodeNm");
        String growthTemp = getTagValue(detailDocument, "grwhTpCodeNm");
        String winterLowTemp = getTagValue(detailDocument, "winterLwetTpCodeNm");
        String humidity = getTagValue(detailDocument, "hdCodeNm");
        String fertilizerInfo = getTagValue(detailDocument, "frtlzrInfo");
        String waterSpring = getTagValue(detailDocument, "watercycleSprngCodeNm");
        String waterSummer = getTagValue(detailDocument, "watercycleSummerCodeNm");
        String waterAutumn = getTagValue(detailDocument, "watercycleAutumnCodeNm");
        String waterWinter = getTagValue(detailDocument, "watercycleWinterCodeNm");

        return PlantApiResDto.builder()
                .codeNm(codeNm)
                .cntntsNo(cntntsNo)
                .cntntsSj(cntntsSj)
                .smell(smell != null && !smell.isEmpty() ? smell : "N/A")
                .toxicity(toxicity != null && !toxicity.isEmpty() ? toxicity : "N/A")
                .managementLevel(managementLevel != null && !managementLevel.isEmpty() ? managementLevel : "N/A")
                .growthSpeed(growthSpeed != null && !growthSpeed.isEmpty() ? growthSpeed : "N/A")
                .growthTemperature(growthTemp != null && !growthTemp.isEmpty() ? growthTemp : "N/A")
                .winterLowestTemp(winterLowTemp != null && !winterLowTemp.isEmpty() ? winterLowTemp : "N/A")
                .humidity(humidity != null && !humidity.isEmpty() ? humidity : "N/A")
                .fertilizerInfo(fertilizerInfo != null && !fertilizerInfo.isEmpty() ? fertilizerInfo : "N/A")
                .waterSpring(waterSpring != null && !waterSpring.isEmpty() ? waterSpring : "N/A")
                .waterSummer(waterSummer != null && !waterSummer.isEmpty() ? waterSummer : "N/A")
                .waterAutumn(waterAutumn != null && !waterAutumn.isEmpty() ? waterAutumn : "N/A")
                .waterWinter(waterWinter != null && !waterWinter.isEmpty() ? waterWinter : "N/A")
                .build();
    }


    public static String getTagValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent().trim();
        }
        return null;
    }


    public static NodeList getNodesByTagName(Document document, String tagName) {
        return document.getElementsByTagName(tagName);
    }


    public static String extractValue(Node node, String tagName) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            return getTagValue(element, tagName);
        }
        return null;
    }
}
