package com.toannq.test.core.controller;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.SilentJavaScriptErrorListener;
import com.toannq.test.commons.exception.BusinessException;
import com.toannq.test.commons.util.ErrorCode;
import com.toannq.test.commons.util.Pair;
import com.toannq.test.core.model.response.StudentResponse;
import com.toannq.test.core.service.StudentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RestController
@RequestMapping("/tests")
@Validated
public class TestController {
    private final SpringTemplateEngine htmlTemplateEngine;
    private final StudentService studentService;

    public TestController(SpringTemplateEngine htmlTemplateEngine, StudentService studentService) {
        this.htmlTemplateEngine = htmlTemplateEngine;
        this.studentService = studentService;
    }

    @GetMapping
    public String render(@RequestParam @Valid @NotNull BigDecimal money) {
        var context = new Context();
        context.setVariable("money", money);
        context.setVariable("createdAt", new Date());
        return htmlTemplateEngine.process("/test.html", context);
    }

    @GetMapping("/students/{id}")
    public CompletableFuture<StudentResponse> getStudent(@PathVariable @Valid @NotNull Long id) {
        return studentService.get(id);
    }

    @GetMapping("/my-ip")
    public CompletableFuture<String> getMyIp(@RequestParam("proxy") @Valid @NotBlank String proxy) {
        var extracted = proxy.split(":");
        var proxyHost = extracted[0] + ":" + extracted[1];
        var proxyPort = extracted.length == 2 ? 80 : Integer.parseInt(extracted[2]);
//        var webClient = new WebClient(BrowserVersion.CHROME, proxyHost, proxyPort);
        var webClient = new WebClient(BrowserVersion.CHROME);
        webClient.setJavaScriptErrorListener(new SilentJavaScriptErrorListener());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        final var pageUrl = "https://www.google.com/";
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return (HtmlPage) webClient.getPage(pageUrl);
                    } catch (IOException e) {
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Can not open page: " + pageUrl);
                    }
                })
                .thenApply(htmlPage -> {
                    var elements = htmlPage.getByXPath("//*[@id=\"APjFqb\"]");
                    if (elements.isEmpty()) {
                        throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "Data not found at page: " + pageUrl);
                    }
                    return Pair.of(htmlPage, (HtmlTextArea) elements.get(0));
                })
                .thenApply(pair -> {
                    var htmlTextArea = pair.second();
                    htmlTextArea.setText("what is my ip");
                    return pair.first();
                })
                .thenApply(htmlPage -> Optional.ofNullable((HtmlSubmitInput) htmlPage.getFirstByXPath("/html/body/div[1]/div[3]/form/div[1]/div[1]/div[4]/center/input[1]"))
                        .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Data not found at page: " + pageUrl)))
                .thenApply(htmlSubmitInput -> {
                    try {
                        return (HtmlPage) htmlSubmitInput.click();
                    } catch (IOException e) {
                        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Can not open page after perform action click at: " + htmlSubmitInput.getValue());
                    }
                })
                .thenApply(htmlPage -> Optional.ofNullable((HtmlDivision) htmlPage.getFirstByXPath("//*[@id=\"rso\"]/div[1]/div/div/div/div"))
                        .map(div -> (HtmlSpan) div.getFirstByXPath("//div/div/form/div/div/span/span"))
                        .orElseThrow(() -> new BusinessException(ErrorCode.DATA_NOT_FOUND, "Data not found at page: " + pageUrl)))
                .thenApply(DomNode::asNormalizedText)
                .whenComplete((response, throwable) -> {
                    webClient.close();
                    if (throwable != null) {
                        throw (CompletionException) throwable;
                    }
                });
    }

}
