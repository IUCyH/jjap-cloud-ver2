package com.iucyh.jjapcloudimprove.service.music;

import com.iucyh.jjapcloudimprove.domain.music.Music;
import com.iucyh.jjapcloudimprove.dto.music.CreateMusicDto;
import com.iucyh.jjapcloudimprove.dto.music.MusicDto;
import com.iucyh.jjapcloudimprove.dto.music.UpdateMusicDto;
import com.iucyh.jjapcloudimprove.dto.music.query.MusicSimpleDto;
import com.iucyh.jjapcloudimprove.dtomapper.music.MusicDtoMapper;
import com.iucyh.jjapcloudimprove.repository.music.MusicQueryRepository;
import com.iucyh.jjapcloudimprove.repository.music.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MusicService {

    private static final long pagingLimit = 50;
    private final MusicRepository musicRepository;
    private final MusicQueryRepository musicQueryRepository;

    public List<MusicDto> findMusics(LocalDateTime date) {
        List<MusicSimpleDto> musics = musicQueryRepository.findMusics(date, pagingLimit);
        return musics.stream()
                .map(MusicDtoMapper::toMusicDto)
                .toList();
    }

    /**
     * Don't use it directly, Please use it through (MusicFacade).save method.
     * @param userId
     * @param storeResult
     * @param musicDto
     * @return Saved entity's public id
     */
    @Transactional
    public String createMusic(Long userId, MusicFileStoreResult storeResult, CreateMusicDto musicDto) {
        Music music = Music.of(musicDto.getTitle(), storeResult.getStoreName(), storeResult.getPlayTime());
        musicRepository.save(music);
        return music.getPublicId();
    }

    @Transactional
    public void updateMusic(Long userId, String musicPublicId, UpdateMusicDto musicDto) {
        Music foundMusic = musicRepository.findByPublicId(musicPublicId);
        foundMusic.update(musicDto.getTitle());
    }

    @Transactional
    public void deleteMusic(Long userId, String musicPublicId) {
        Music foundMusic = musicRepository.findByPublicId(musicPublicId);
        foundMusic.softDelete();
    }
}
