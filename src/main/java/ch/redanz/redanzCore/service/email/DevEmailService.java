package ch.redanz.redanzCore.service.email;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("dev")
public class DevEmailService {
}
