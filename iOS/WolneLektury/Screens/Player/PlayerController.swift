//
//  PlayerController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 21/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import AVFoundation
import MediaPlayer

protocol PlayerControllerDelegate: class {
    func playerControllerDelegateTrackChanged()
    func playerControllerDelegateUpdatePlayerProgress(timeInterval: TimeInterval)
    func playerControllerDelegatePlayStateChanged()
    
}

class PlayerController: UIResponder, AVAudioPlayerDelegate {
    static let shared = PlayerController()

    override init() {
        super.init()

        let commandCenter = MPRemoteCommandCenter.shared()
        commandCenter.playCommand.isEnabled = true
        commandCenter.pauseCommand.isEnabled = true
        commandCenter.nextTrackCommand.isEnabled = true
        commandCenter.previousTrackCommand.isEnabled = true
        commandCenter.nextTrackCommand.addTarget(self, action: #selector(PlayerController.nextTrackCommand))
        commandCenter.previousTrackCommand.addTarget(self, action: #selector(PlayerController.previousTrackCommand))
        commandCenter.playCommand.addTarget(self, action: #selector(PlayerController.playCommand))
        commandCenter.pauseCommand.addTarget(self, action: #selector(PlayerController.pauseCommand))
        if #available(iOS 9.1, *) {
            commandCenter.changePlaybackPositionCommand.addTarget(self, action: #selector(PlayerController.changePlaybackPositionCommand(event:)))
        } else {
            // Fallback on earlier versions
        }
    }
    
    weak var delegate: PlayerControllerDelegate?
    
    private(set) var currentBookDetails: BookDetailsModel? {
        didSet {
            if let currentBookDetails = currentBookDetails {
                audiobookMediaModels = currentBookDetails.getAudiobookMediaModels()
                if currentBookDetails.currentAudioChapter < audiobookMediaModels!.count {
                    currentAudiobookIndex = currentBookDetails.currentAudioChapter
                }
                else {
                    currentAudiobookIndex = 0
                }
            }
            else{
                audiobookMediaModels = nil
                currentAudiobookIndex = 0
            }
        }
    }
    
    private var audiobookMediaModels: [MediaModel]?
    private(set) var currentAudiobookIndex: Int = 0
    var audioPlayer:AVAudioPlayer? = nil
    var timer:Timer?
    var audioLength = 0.0
    var currentAudioPath: URL?
    private(set) var artwork: MPMediaItemArtwork?{
        didSet{
            updateMediaInfo()
        }
    }
        
    func setCoverImage(image: UIImage){
        artwork = MPMediaItemArtwork(image: image)
    }
    
    func getCurrentTime() -> TimeInterval {
        guard let audioPlayer = audioPlayer else { return 0 }
        return audioPlayer.currentTime
    }
    
    func isPlaying() -> Bool {
        guard let audioPlayer = audioPlayer else { return false }
        return audioPlayer.isPlaying
    }

    func stopAndClear() {
        stopTimer()
        audioPlayer?.stop()
        currentBookDetails = nil
    }
    
    func preparePlayer(bookDetailsModel: BookDetailsModel, delegate: PlayerControllerDelegate, trackIndex: Int?) {
        stopAndClear()
        
        self.delegate = delegate
        currentBookDetails = bookDetailsModel
        if let trackIndex = trackIndex {
            currentAudiobookIndex = trackIndex
        }
        else if bookDetailsModel.currentAudioChapter < audiobookMediaModels!.count {
            currentAudiobookIndex = bookDetailsModel.currentAudioChapter
        }
        prepareAudio()
        delegate.playerControllerDelegateTrackChanged()
    }
    
    func startOrContinuePlaying(bookDetailsModel: BookDetailsModel, delegate: PlayerControllerDelegate) {
        guard let audioPlayer = audioPlayer, let currentBookDetails = currentBookDetails, currentBookDetails.slug == bookDetailsModel.slug else {
            startPlaying(bookDetailsModel: bookDetailsModel, delegate: delegate, trackIndex: nil)
            return
        }
        
        
        self.delegate = delegate
        if audioPlayer.isPlaying {
            delegate.playerControllerDelegateTrackChanged()
        }
        else {
            prepareAudio()
            playAudio()

        }
    }

    func startPlaying(bookDetailsModel: BookDetailsModel, delegate: PlayerControllerDelegate, trackIndex: Int?) {
        preparePlayer(bookDetailsModel: bookDetailsModel, delegate: delegate, trackIndex: trackIndex)
        playAudio()
    }
    
    func getCurentAudiobookMediaModel() -> MediaModel? {
        guard let audiobookMediaModels = audiobookMediaModels, audiobookMediaModels.count > currentAudiobookIndex else { return nil }
        
        return audiobookMediaModels[currentAudiobookIndex]
    }
    
    func updateMediaInfo(){
        guard let audioPlayer = audioPlayer, let model = getCurentAudiobookMediaModel() else { return }
        
        let artistName = model.artist
        let songName = model.name
        
        if let artwork = artwork {
            MPNowPlayingInfoCenter.default().nowPlayingInfo = [MPMediaItemPropertyArtist : artistName,  MPMediaItemPropertyTitle : songName, MPMediaItemPropertyPlaybackDuration:  audioLength, MPNowPlayingInfoPropertyElapsedPlaybackTime: audioPlayer.currentTime, MPMediaItemPropertyArtwork: artwork]
        }
        else {
            MPNowPlayingInfoCenter.default().nowPlayingInfo = [MPMediaItemPropertyArtist : artistName,  MPMediaItemPropertyTitle : songName, MPMediaItemPropertyPlaybackDuration:  audioLength, MPNowPlayingInfoPropertyElapsedPlaybackTime: audioPlayer.currentTime]
        }
    }
    
    // Prepare audio for playing
    func prepareAudio(){
        guard let currentBook = currentBookDetails, let currentMediaModel = getCurentAudiobookMediaModel() else { return }
        
        guard let path = NSObject.audiobookPathIfExists(audioBookUrlString: currentMediaModel.url, bookSlug: currentBook.slug) else { return }
        
        currentAudioPath = path
        audioPlayer = try? AVAudioPlayer(contentsOf: path)

        guard let audioPlayer = audioPlayer else { return }
        
        do {
            //keep alive audio at background
            try AVAudioSession.sharedInstance().setCategory(AVAudioSessionCategoryPlayback)
        } catch _ {
        }
        do {
            try AVAudioSession.sharedInstance().setActive(true)
        } catch _ {
        }
        UIApplication.shared.beginReceivingRemoteControlEvents()
        audioPlayer.delegate = self
        audioLength = audioPlayer.duration
        audioPlayer.prepareToPlay()
        
    }
    
    //MARK:- Player Controls Methods
    func  playAudio(){
        audioPlayer?.play()
        startTimer()
        saveCurrentTrackNumber()
        updateMediaInfo()
        delegate?.playerControllerDelegateTrackChanged()
    }
    
    func playNextAudio(){
        guard let audiobookMediaModels = audiobookMediaModels, currentAudiobookIndex < (audiobookMediaModels.count  - 1) else { return }
        
        currentAudiobookIndex += 1
        
        prepareAudio()
        playAudio()
    }
    
    func playPreviousAudio(){
        
        guard currentAudiobookIndex > 0 else { return }

        currentAudiobookIndex -= 1
        
        prepareAudio()
        playAudio()
    }
    
    func forward() {
        guard let audioPlayer = audioPlayer, audioPlayer.isPlaying else { return }
        if audioPlayer.currentTime + 12 < audioPlayer.duration {
            audioPlayer.currentTime += 10
            updateMediaInfo()
        }
        else {
            playNextAudio()
        }
    }

    func rewind() {
        guard let audioPlayer = audioPlayer, audioPlayer.isPlaying else { return }
        if audioPlayer.currentTime > 10 {
            audioPlayer.currentTime -= 10
            updateMediaInfo()
        }
        else {
            playPreviousAudio()
        }
    }
        
    func pauseAudioPlayer(){
        audioPlayer?.pause()
        updateMediaInfo()
        delegate?.playerControllerDelegatePlayStateChanged()
    }
    
    func playAudioPlayer(){
        audioPlayer?.play()
        updateMediaInfo()
        delegate?.playerControllerDelegatePlayStateChanged()
    }
    
    func changeTime(timeInterval: TimeInterval) {
        guard let audioPlayer = audioPlayer else { return }
        if audioLength > timeInterval {
            audioPlayer.currentTime = timeInterval
        }
        updateMediaInfo()
    }
    
    func saveCurrentTrackNumber(){
        guard let bookDetailsModel = currentBookDetails else { return }
        
        DatabaseManager.shared.updateCurrentChapters(bookDetailsModel: bookDetailsModel, currentChapter: nil, totalChapters: nil, currentAudioChapter: currentAudiobookIndex, totalAudioChapters: nil)
    }


    func startTimer(){
        if timer == nil {
            timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(PlayerController.update(_:)), userInfo: nil,repeats: true)
            timer?.fire()
        }
        else if timer!.isValid == false {
            timer?.invalidate()
            timer = nil
            
            timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(PlayerController.update(_:)), userInfo: nil,repeats: true)
            timer?.fire()
        }
    }
    
    func stopTimer(){
        timer?.invalidate()
        
    }
    
    @objc func update(_ timer: Timer){
        guard let audioPlayer = audioPlayer else { return }
        if !audioPlayer.isPlaying{
            return
        }
        
        delegate?.playerControllerDelegateUpdatePlayerProgress(timeInterval: audioPlayer.currentTime)
    }
    
    
    /* audioPlayerDidFinishPlaying:successfully: is called when a sound has finished playing. This method is NOT called if the player is stopped due to an interruption. */
    public func audioPlayerDidFinishPlaying(_ player: AVAudioPlayer, successfully flag: Bool) {
        if flag == true {
            playNextAudio()
        }
    }

    @objc func changePlaybackPositionCommand(event: MPChangePlaybackPositionCommandEvent) -> MPRemoteCommandHandlerStatus{
        guard let audioPlayer = audioPlayer else { return .commandFailed }
        
        if audioPlayer.duration - 5 < event.positionTime && audioPlayer.duration > 5 {
            audioPlayer.currentTime = audioPlayer.duration - 5
        }
        else {
            audioPlayer.currentTime = event.positionTime
        }
        updateMediaInfo()
        return .success
    }

    @objc func nextTrackCommand() -> Void{
        forward()
    }
    
    @objc func previousTrackCommand() -> Void{
        rewind()
    }
    
    @objc func playCommand() -> Void{
        playAudioPlayer()
    }
    
    @objc func pauseCommand() -> Void{
        pauseAudioPlayer()
    }
}
