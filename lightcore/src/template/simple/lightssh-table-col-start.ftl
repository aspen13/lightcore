<td <#rt/>
<#if sortedSubstitute.cssClass?exists>
 class="${ sortedSubstitute.cssClass?html}"<#rt/>
</#if>
<#include "/${sortedSubstitute.templateDir}/simple/scripting-events.ftl" />
><@s.property value="stack.findValue( sortedSubstitute.value )"/>