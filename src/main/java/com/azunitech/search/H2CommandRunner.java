package com.azunitech.search;

import com.azunitech.search.database.H2Manager;
import com.azunitech.search.database.TutorialRepository;
import com.azunitech.search.domain.Tutorial;
import com.azunitech.search.domain.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class H2CommandRunner implements CommandLineRunner {
    @Autowired
    TutorialRepository tutorialRepository;

    @Override
    public void run(String... args) throws Exception {
        tutorialRepository.save(Tutorial.builder()
                .id(1)
                .title("t")
                .published(true)
                .description("desc")
                .build());
        tutorialRepository.findAll()
                .forEach(x -> log.info(x.toString()));


        H2Manager h2Manager = H2Manager.builder()
                .build();
        h2Manager.insert(User.builder()
                .id(1)
                .author("a")
                .title("t")
                .build());
        h2Manager.query()
                .forEach(x -> log.info(x.toString()));
    }
}
