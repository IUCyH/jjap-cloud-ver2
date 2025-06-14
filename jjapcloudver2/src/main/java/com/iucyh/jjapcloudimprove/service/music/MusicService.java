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

    @Transactional
    public String createMusic(Long userId, CreateMusicDto musicDto) {
        // TODO: 파일 저장
        String tempStoreName = UUID.randomUUID().toString().replace("-", "");
        Music music = Music.of(musicDto.getTitle(), tempStoreName, 10);
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
