<#if parameters.value?? && stack.findValue(parameters.value)??>
<div <#rt/>
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
	<#assign pagination=stack.findValue(parameters.value) />
	<#if pagination.allSize gt 0>
	
	<#if pagination.number gt 1>
		<a class="previous" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${pagination.number-1}">&laquo; 上一页</a> 
	</#if>
	
	<#assign startPage=stack.findValue("@java.lang.Math@max(${pagination.number-pageLeftLength},1)") />
	<#assign endPage=stack.findValue("@java.lang.Math@min(${pagination.number+pageRightLength},${pagination.allPage})") />
	
	<#if startPage == 1>
		<#assign endPage=stack.findValue("@java.lang.Math@min(${pageLength},${pagination.allPage})") />
	</#if>
	<#if endPage == pagination.allPage>
		<#assign startPage=stack.findValue("@java.lang.Math@max(${endPage-pageLength},1)") />
	</#if>
	
	<#if startPage gt 1>
		<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=1">1</a>
		<#if startPage == 3>
			<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=2">2</a>
		<#elseif startPage gt 2 >
			<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${(startPage/2+1)?int}">...</a>
		</#if>
	</#if>
	
	<@s.iterator value="new int[${endPage - startPage + 1}]" status="loop">	
		<#assign pageIndex=startPage+loop.index/>
		<#if pagination.allPage gt 1>
			<#if pageIndex == pagination.number>
				<span class="number current" title="page ${pageIndex}">${pageIndex}</span>
			<#else>
				<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${pageIndex}">${pageIndex}</a>
			</#if>
		</#if>
	</@s.iterator>
	
	<#if endPage != pagination.allPage>
		<#if endPage == pagination.allPage-2>
			<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${pagination.allPage-1}">${pagination.allPage-1}</a>
		<#elseif endPage <pagination.allPage-1>
			<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${endPage+(pagination.allPage-endPage)/2?int}">...</a>
		</#if>
		<a class="number" href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${pagination.allPage}">${pagination.allPage}</a>
	</#if>
	
	<#if pagination.number lt pagination.allPage>
		<a class="next"  href="${parameters.pageNumberQueryParams}&${pageNumberParamName}=${pagination.number+1}">下一页 &raquo;</a> 
	</#if>
	共${pagination.allSize}条记录，${pagination.allPage}页
	
	，每页显示
	<select onchange="location.href='${parameters.pageSizeQueryParams}&${pageSizeParamName}='+this.value">
		<#list pageSizeArray as item>
			<option value="${item}" <#if item==pagination.size>selected</#if> >${item}</option>
		</#list>
	</select>
	</#if>
</div>
</#if>