package com.sparta.finalticket.domain.game.service;

import com.sparta.finalticket.domain.game.dto.request.GameRequestDto;
import com.sparta.finalticket.domain.game.dto.response.GameResponseDto;
import com.sparta.finalticket.domain.game.entity.CategoryEnum;
import com.sparta.finalticket.domain.game.entity.Game;
import com.sparta.finalticket.domain.game.entity.PlaceEnum;
import com.sparta.finalticket.domain.game.repository.GameRepository;
import com.sparta.finalticket.domain.seat.dto.SeatSettingResponseDto;
import com.sparta.finalticket.domain.seat.entity.Seat;
import com.sparta.finalticket.domain.seat.repository.SeatRepository;
import com.sparta.finalticket.domain.seatsetting.entity.SeatSetting;
import com.sparta.finalticket.domain.seatsetting.repository.SeatSettingRepository;
import com.sparta.finalticket.domain.user.entity.User;
import com.sparta.finalticket.domain.user.entity.UserRoleEnum;
import com.sparta.finalticket.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

  private final GameRepository gameRepository;
  private final UserRepository userRepository;
  private final SeatRepository seatRepository;
  private final SeatSettingRepository seatSettingRepository;

  public GameResponseDto createGame(GameRequestDto gameRequestDto, User user) {
    validateCheckAdmin(user);

    Game game = gameRepository.save(
        Game.builder()
            .name(gameRequestDto.getName())
            .place(PlaceEnum.valueOf(gameRequestDto.getPlace()))
            .category(CategoryEnum.valueOf(gameRequestDto.getCategory()))
            .count(gameRequestDto.getCount())
            .startDate(gameRequestDto.getStartDate())
            .state(true)
            .user(user)
            .build());


    return GameResponseDto.builder()
        .id(game.getId())
        .name(game.getName())
        .count(game.getCount())
        .place(String.valueOf(game.getPlace()))
        .category(String.valueOf(game.getCategory()))
        .startDate(game.getStartDate())
        .build();
  }

  public GameResponseDto updateGame(GameRequestDto gameRequestDto, User user, Long gameId) {
    validateCheckAdmin(user);
    Game game = validateExistGame(gameId);

    game.setName(gameRequestDto.getName());
    game.setCategory(CategoryEnum.valueOf(gameRequestDto.getCategory()));
    game.setCount(gameRequestDto.getCount());
    game.setStartDate(gameRequestDto.getStartDate());
    game.setPlace(PlaceEnum.valueOf(gameRequestDto.getPlace()));

    return GameResponseDto.builder()
        .id(gameId)
        .name(game.getName())
        .category(String.valueOf(game.getCategory()))
        .count(game.getCount())
        .startDate(game.getStartDate())
        .place(String.valueOf(game.getPlace()))
        .build();
  }

  public void deleteGame(Long gameId, User user) {
   validateCheckAdmin(user);
    Game game = validateExistGame(gameId);
    game.deleteGame();
    gameRepository.deleteGameAndRelateEntities(gameId);
  }


  @Transactional(readOnly = true)
  public List<GameResponseDto> getAllGameList() {
    return gameRepository.findAll().stream()
        .map(GameResponseDto::new).toList();
  }

  @Transactional(readOnly = true)
  public GameResponseDto getGame(Long gameId) {
    Game game = validateExistGame(gameId);
    return new GameResponseDto(game);
  }


  private void validateCheckAdmin(User user) {
    if (user.getRole() != UserRoleEnum.ADMIN) {
      throw new IllegalArgumentException("관리자만 경기를 등록,수정,삭제할 수 있습니다.");
    }
  }

  private Game validateExistGame(Long gameId) {
    return gameRepository.findByIdAndStateTrue(gameId).orElseThrow(
        () -> new IllegalArgumentException("해당 게임을 찾을 수 없습니다.")
    );
  }

  public List<GameResponseDto> getUserGameList(User user) {
    return gameRepository.getUserGameList(user);
  }

  //예매예정경기 전체 조회
  public List<GameResponseDto> getUpcomingGame() {
    List<Game> gameList = getGames();
    return filterGames(game -> game.getStartDate().isAfter(LocalDateTime.now()));
  }

  //예매가능경기 전체 조회
  public List<GameResponseDto> getAvailableGame() {
    List<Game> gameList = getGames();
    return filterGames(game -> game.getStartDate().isBefore(LocalDateTime.now()));
  }

  private List<Game> getGames() {
    List<Game> gameList = gameRepository.findAll();
    if (gameList.isEmpty()) {
      throw new IllegalArgumentException("조회할 수 있는 게임이 없습니다.");
    }
    return gameList;
  }

  private List<GameResponseDto> filterGames(Predicate<Game> condition) {
    return getGames().stream()
        .filter(condition)
        .map(GameResponseDto::new)
        .toList();
  }

  public List<SeatSettingResponseDto> getSeat(Long id) {
    List<Seat> seatList = seatRepository.findALlByGameIdAndStateTrue(id).stream().toList();
    List<SeatSetting> seatSettingList = seatSettingRepository.findAll();
    return comparedSeat(seatList, seatSettingList).stream().map(SeatSettingResponseDto::new).toList();
  }

  public List<SeatSetting> comparedSeat(List<Seat> seatList, List<SeatSetting> seatSettingList){
    List<SeatSetting> remainingSeatSettings = new ArrayList<>(seatSettingList);
    for(Seat seat : seatList){
      String seatNumber = seat.getSeatsetting().getSeatNumber();
      Iterator<SeatSetting> iterator = remainingSeatSettings.iterator();
      while (iterator.hasNext()) {
        SeatSetting seatSetting = iterator.next();
        if (seatSetting.getSeatNumber().equals(seatNumber)) {
          iterator.remove();
        }
      }
    }
    return remainingSeatSettings;
  }

  public List<GameResponseDto> getGameOfCategory(CategoryEnum category){
    return gameRepository.getGameOfCategory(category);
  }

  public List<GameResponseDto> getGameOfKeyword(String keyword) {
    return gameRepository.getGameOfKeyword(keyword);
  }
}
