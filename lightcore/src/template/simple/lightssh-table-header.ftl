
<table <#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
<#else>
 class="pagination"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
>
<colgroup>
	<#list parameters.columns as column>
	<col class="element" <#if column.width?exists>width="${column.width}"</#if>/>
	</#list>
</colgroup>
<thead>
	<tr>
	<#list parameters.columns as column>
	<th class="<#if column.sortable>sortable 
		<#if (column.possibleSortKey?exists) &&parameters.orderByPropertyValue?exists && column.possibleSortKey == parameters.orderByPropertyValue> ${parameters.orderByTypeValue?lower_case} active</#if>
	</#if> 
	<#if column.cssClass?exists>${column.cssClass}</#if>" 
	<#if (column.possibleSortKey?exists) && column.sortable> onclick="window.document.location='${pageOrderByQueryParams}&${pageOrderByPropertyParamName}=${column.possibleSortKey}&${pageOrderByTypeParamName}=${parameters.toggleOrderByTypeValue}'"</#if>
	>
		<#if column.sortable><span>&nbsp;</span></#if><@s.text name="${column.title}"/>
	</th>
	</#list>
	</tr>
</thead>

<tbody>
