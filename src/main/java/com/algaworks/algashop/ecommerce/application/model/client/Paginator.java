package com.algaworks.algashop.ecommerce.application.model.client;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

public class Paginator {

    public static List<PageLinkModel> calculatePages(PageModel pageModel, String path) {
        return calculatePages(pageModel.getTotalPages(), pageModel.getNumber()+1, pageModel.getSize(), path, new LinkedMultiValueMap<>());
    }

    public static List<PageLinkModel> calculatePages(int totalPages, int actualPage, int size, String path, MultiValueMap<String, String> queryParams) {
        List<PageLinkModel> pagesToShow = new ArrayList<>();

        // Verifica se o número total de páginas é menor que 5
        int maxPages = 5;
        if (totalPages <= maxPages) {
            for (int i = 1; i <= totalPages; i++) {
                pagesToShow.add(new PageLinkModel(path, size, i, i == actualPage,
                        i == 1, i == totalPages, i < totalPages, i > 1, queryParams));
            }
        } else {
            // Se a página atual estiver próxima do início
            if (actualPage <= 3) {
                for (int i = 1; i <= maxPages; i++) {
                    pagesToShow.add(new PageLinkModel(path, size, i, i == actualPage,
                            i == 1, i == totalPages, i < totalPages, i > 1, queryParams));
                }
            }
            // Se a página atual estiver próxima do final
            else if (actualPage > totalPages - 3) {
                for (int i = totalPages - 4; i <= totalPages; i++) {
                    pagesToShow.add(new PageLinkModel(path, size, i, i == actualPage,
                            i == 1, i == totalPages, i < totalPages, i > 1, queryParams));
                }
            }
            // Se a página atual estiver no meio
            else {
                for (int i = actualPage - 2; i <= actualPage + 2; i++) {
                    pagesToShow.add(new PageLinkModel(path, size, i, i == actualPage,i == 1,
                            i == totalPages, i < totalPages, i > 1, queryParams));
                }
            }
        }

        return pagesToShow;
    }

    public static List<PageLinkModel> calculatePages(PageModel pageModel, String path, MultiValueMap<String, String> queryParams) {
		return calculatePages(pageModel.getTotalPages(), pageModel.getNumber() + 1, pageModel.getSize(), path, queryParams);
    }
}