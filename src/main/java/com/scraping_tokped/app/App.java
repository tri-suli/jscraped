package com.scraping_tokped.app;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App 
{
    public static void main(String[] args)
    {
        int page = 1;
        final int MAX_PRODUCT = 5;
        final String domain = "https://tokopedia.com";
        List<Map <String, String>> products = new ArrayList<Map<String, String>>();

        try {
            do {
                String query = "/search?q=handphone&rt=4%2C5&source=universe&srp_component_id=02.07.02.01&st=product";

                if (page > 1) {
                    query += "&page=" + page;
                }

                final String url = domain + query;
                Document document = Jsoup.connect(url).get();
                Elements newsHeadlines = document.select(".pcv3__container");

                for (Element headline : newsHeadlines) {
                    String title = headline.getElementsByAttributeValueMatching("data-testId", "spnSRPProdName").text();
                    String descriptionLink = headline.select(".pcv3__info-content").attr("href");
                    String imageLink = headline.getElementsByAttributeValueMatching("data-testId", "imgSRPProdMain")
                                                .select("img")
                                                .attr("src");
                    String price = headline.getElementsByAttributeValueMatching("data-testId", "spnSRPProdPrice").text();
                    float rating = Float.parseFloat(headline.select(".css-8zsigz .css-1ffszw6").text());
                    String merchant = headline.getElementsByAttributeValueMatching("data-testId", "shopWrapper").select(".css-1rn0irl").text();
                    
                    if (
                        !title.isEmpty() &&
                        !descriptionLink.contains("ta.tokopedia") &&
                        (!imageLink.isEmpty() && !imageLink.contains("data:image")) &&
                        !price.isEmpty() &&
                        rating >= 5 &&
                        !merchant.isEmpty()
                    ) {
                        Document descriptionDoc = Jsoup.connect(descriptionLink).get();
                        String description = descriptionDoc.getElementsByAttributeValueMatching("data-testid", "lblPDPDescriptionProduk").text();

                        if (description.length() > 0) {
                            System.out.println("=====================");
                            System.out.println(title);
                            System.out.println(description);
                            System.out.println(imageLink);
                            System.out.println(price);
                            System.out.println(rating);
                            System.out.println(merchant);
                            System.out.println("=====================");
    
                            Map<String, String> product = new HashMap<String, String>();
    
                            product.put("title", title);
                            product.put("desc", description);
                            product.put("image", imageLink);
                            product.put("price", price);
                            product.put("rating", String.format("%.2f", rating));
                            product.put("merchant", merchant);
                            products.add(product);
                        }
                    }
                }

                page += 1;
            } while (products.size() < MAX_PRODUCT);

            ICSVWriter writer = new CSVWriterBuilder(
                new FileWriter("report.csv")
            ).withSeparator(';').build();

            String[] header = {
                "Title", "Description", "Image Link", "Price", "Rating (out of 5 stars)", "Merchant"
            };
            List<String[]> reports = new ArrayList<String[]>();
            reports.add(header);

            for (Map<String, String> product : products ) {
                String[] report = {
                    product.get("title"),
                    product.get("desc"),
                    product.get("image"),
                    product.get("price"),
                    product.get("rating"),
                    product.get("merchant")
                };

                reports.add(report);
            }

            writer.writeAll(reports);
            System.out.println( "Done!" );
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
