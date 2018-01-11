<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:functx="http://www.functx.com" xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="functx xs">
	<xsl:output indent="yes"/>
    <xsl:template match="/">
        <xsl:element name="WorkFlows">
            <xsl:apply-templates select="//java/object/void"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="void[@method='add']">
			<xsl:element name="WorkFlow">
				<xsl:attribute name="id">
					<xsl:value-of select="./object/void[string[1]='SOAPFilter_id']/string[last()]"/>
				</xsl:attribute>
				<xsl:apply-templates select="./object/void"/>
			</xsl:element>
    </xsl:template>
    <xsl:template match="void[@method='put']">
		<xsl:variable name="tagName">
			<xsl:value-of select="./string[1]"/>
		</xsl:variable>
		<xsl:variable name="tagValue">
			<xsl:value-of select="./string[last()]"/>
		</xsl:variable>
		<xsl:element name="{$tagName}" >
			<xsl:choose>
				<xsl:when test="(substring($tagValue,1,1)='&lt;')">
					<xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
						<xsl:value-of disable-output-escaping="yes" select="$tagValue"/>
					<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$tagValue"/>
				</xsl:otherwise>
			</xsl:choose>			
		</xsl:element>
    </xsl:template>
</xsl:stylesheet>