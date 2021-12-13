package com.scraping_tokped.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App 
{
    public static void main( String[] args )
    {
        final String url = "https://www.tokopedia.com/search?navsource=home&source=universe&srp_component_id=02.02.02.01&st=product&q=handphone";

        try {
            
            Document document = Jsoup.connect(url).get();
            log(document.title());

            Elements elements = document.select(".css-12sieg3");

            for (Element element : elements) {
                log("%s\n\t%s", element.html());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        System.out.println( "Hello World!" );
    }

    private static void log(String msg, String... vals) {
        System.out.println(String.format(msg, vals));
    }
}
