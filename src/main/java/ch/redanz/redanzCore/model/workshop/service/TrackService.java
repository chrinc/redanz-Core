package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Track;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrackService {

    private final TrackRepo trackRepo;

    @Autowired
    public TrackService(TrackRepo trackRepo) {
        this.trackRepo = trackRepo;
    }

    public Track findByTrackId(Long trackId) {
        return trackRepo.findByTrackId(trackId);
    }
}
