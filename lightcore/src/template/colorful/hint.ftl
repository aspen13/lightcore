<#assign hasFieldErrors = parameters.name?? && fieldErrors?? && fieldErrors[parameters.name]??/>

<#if hasFieldErrors>
<span class="error">
    ${fieldErrors[parameters.name][0]}
</span>
</#if>
