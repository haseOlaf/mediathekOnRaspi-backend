package mediathekRaspi.mediatheView.service;

import mediathekRaspi.mediatheView.exception.ServerUnaialableException;
import mediathekRaspi.mediatheView.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;

@Service
public class MediatheViewService {


    @Value("${mediathekview.baseUrl}")
    private String baseUrl;

    Logger LOG = LoggerFactory.getLogger(MediatheViewService.class);


    public List<Video> getVideos(String channel, String title, String topic) {
        MediatheViewQuery mediatheViewQuery = new MediatheViewQuery(0);
        mediatheViewQuery.setQueries(new ArrayList<>());

        if (!isEmpty(channel)) {
            Filter filter = new Filter();
            filter.setFields(new ArrayList<>());
            filter.getFields().add("channel");
            filter.setQuery(channel);
            mediatheViewQuery.getQueries().add(filter);
        }

        if (!isEmpty(title)) {
            Filter filter = new Filter();
            filter.setFields(new ArrayList<>());
            filter.getFields().add("title");
            filter.setQuery(title);
            mediatheViewQuery.getQueries().add(filter);
        }

        if (!isEmpty(topic)) {
            Filter filter = new Filter();
            filter.setFields(new ArrayList<>());
            filter.getFields().add("topic");
            filter.setQuery(topic);
            mediatheViewQuery.getQueries().add(filter);
        }

        LOG.debug("mediatheViewQuery: " + mediatheViewQuery.toString());

        ClientHttpRequestFactory factory = new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(Collections.singletonList(new HeaderRequestInterceptor("Content-Type", "text/plain")));
        restTemplate.getInterceptors().add(new RequestResponseLoggingInterceptor());
        ResponseEntity<MediathekViewAnswer> response =
                restTemplate.postForEntity(baseUrl + "query", mediatheViewQuery, MediathekViewAnswer.class);

        if (response.getStatusCode() == HttpStatus.BAD_REQUEST || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            LOG.error("bad request: " + response.getBody().getErr().toString());
            throw new ServerUnaialableException();
        }

        if (response.getStatusCode() != HttpStatus.OK) {
            LOG.error("http request failed: " + response.getStatusCode() + " " + response.getBody());
            throw new ServerUnaialableException();
        }

        if (response.getBody() == null || response.getBody().getResult() == null ||
                response.getBody().getResult().getResults() == null ||
                response.getBody().getResult().getResults().isEmpty()) {
            return new ArrayList<>();
        }

        return response.getBody().getResult().getResults().stream().map(VideoMapper::map).collect(Collectors.toList());

    }
}
