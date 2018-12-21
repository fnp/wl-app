//
//  PlayerViewController.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 07/09/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import AVFoundation
import MediaPlayer
import Kingfisher

class PlayerViewController: WLViewController {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var bgImageView: UIImageView!
    @IBOutlet weak var bgOverlayView: UIView!
    @IBOutlet weak var miniatureImageView: UIImageView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var toggleListButton: UIButton!
    @IBOutlet weak var nextButton: UIButton!
    @IBOutlet weak var prevButton: UIButton!
    @IBOutlet weak var forwardButton: UIButton!
    @IBOutlet weak var rewindButton: UIButton!
    @IBOutlet weak var playPauseButton: UIButton!
    @IBOutlet weak var trackNumberLabel: UILabel!
    @IBOutlet weak var trackTitleLabel: UILabel!
    @IBOutlet weak var trackCurrentTimeLabel: UILabel!
    @IBOutlet weak var trackTimeLabel: UILabel!
    @IBOutlet weak var progressSlider: UISlider!
    @IBOutlet weak var miniatureImageWidthConstraint: NSLayoutConstraint!
    @IBOutlet weak var trackTitleLabelTopConstraint: NSLayoutConstraint!

    var bookDetailsModel: BookDetailsModel!
    var mediaModels: [MediaModel]!
    var selectedRow: Int?
    
    static func instance(bookDetailsModel: BookDetailsModel) -> PlayerViewController{
        let controller = PlayerViewController.instance()
        controller.bookDetailsModel = bookDetailsModel
        controller.mediaModels = bookDetailsModel.getAudiobookMediaModels()
        return controller
    }

    open override var preferredStatusBarStyle : UIStatusBarStyle {
        return .lightContent
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.separatorStyle = .none
        tableView.isHidden = true
        
        if UIScreen.main.bounds.size.width == 320 || UIScreen.main.bounds.size.height == 320 {
            miniatureImageWidthConstraint.constant = 100
        }
        
        bgOverlayView.alpha = 0.7
        bgOverlayView.backgroundColor = bookDetailsModel.bgColor
        let titleAttributedText = bookDetailsModel.getAttributedAuthorAndTitle(titleFont: UIFont.systemFont(ofSize: 21, weight: .light), descFont: UIFont.systemFont(ofSize: 28, weight: .light))
        titleLabel.attributedText = titleAttributedText
        
        if let bookDetails = PlayerController.shared.currentBookDetails, bookDetails.slug == bookDetailsModel.slug {
            PlayerController.shared.startOrContinuePlaying(bookDetailsModel: bookDetailsModel, delegate: self)
        }
        else {
            PlayerController.shared.stopAndClear()
            PlayerController.shared.preparePlayer(bookDetailsModel: bookDetailsModel, delegate: self, trackIndex: 0)
        }
        
        if mediaModels.count < 2 {
            trackNumberLabel.isHidden = true
            trackNumberLabel.text = ""
            toggleListButton.isHidden = true
            trackTitleLabelTopConstraint.constant = 10
        }

        refreshButtonsVisibility()
        
        if let url = bookDetailsModel.getCoverThumbUrl(){
            ImageDownloader.default.downloadImage(with: url, options: [], progressBlock: nil) {
                [weak self] (image, error, url, data) in
                if let image = image{
                    self?.bgImageView.image = image
                    self?.miniatureImageView.image = image
                    PlayerController.shared.setCoverImage(image: image)
                }
            }
        }
    }
    
    // ############################################
    // MARK: - Private
    // ############################################

    private func toggleList() {
        tableView.isHidden = !tableView.isHidden
    }
    
    private func startNextTrack() {
        PlayerController.shared.playNextAudio()
    }

    private func startPrevTrack() {
        PlayerController.shared.playPreviousAudio()
    }
    
    //This returns song length
    private func calculateTimeFromNSTimeInterval(_ duration:TimeInterval) ->(hour: String, minute:String, second:String){
        let hour_   = abs(Int(duration)/3600)
        let minute_ = abs(Int((duration/60).truncatingRemainder(dividingBy: 60)))
        let second_ = abs(Int(duration.truncatingRemainder(dividingBy: 60)))
        
        var hour = hour_ > 9 ? "\(hour_)" : "0\(hour_)"
        if hour_ == 0 {
            hour = ""
        }
        let minute = minute_ > 9 ? "\(minute_)" : "0\(minute_)"
        let second = second_ > 9 ? "\(second_)" : "0\(second_)"
        return (hour, minute,second)
    }

    fileprivate func updateCurrentTime(timeInterval: TimeInterval?) {
        
        var ti = timeInterval
        if ti == nil {
            ti = PlayerController.shared.getCurrentTime()
        }
        
        if let ti = ti {
            let timeIntervalFloat = Float(ti)
            progressSlider.value = timeIntervalFloat
            let time = calculateTimeFromNSTimeInterval(ti)
            if time.hour.count == 0 {
                trackCurrentTimeLabel.text = "\(time.minute):\(time.second)"
            }
            else {
                trackCurrentTimeLabel.text = "\(time.hour):\(time.minute):\(time.second)"
            }
        }
    }

    func refreshButtonsVisibility(){
        
        prevButton.isHidden = true
        nextButton.isHidden = true
        
        let currentAudiobookIndex = PlayerController.shared.currentAudiobookIndex
        let allMediaCount = mediaModels.count
        
        if currentAudiobookIndex > 0{
            prevButton.isHidden = false
        }
        if allMediaCount > currentAudiobookIndex + 1 {
            nextButton.isHidden = false
        }
    }

    // ############################################
    // MARK: - Action
    // ############################################
    
    @IBAction func backButtonAction(sender: UIButton) {
        PlayerController.shared.delegate = nil
        navigationController?.popViewController(animated: true)
    }
    
    @IBAction func toggleListButtonAction(sender: UIButton) {
        toggleList()
    }
    
    @IBAction func nextButtonAction(sender: UIButton) {
        PlayerController.shared.playNextAudio()
    }
    
    @IBAction func prevButtonAction(sender: UIButton) {
        PlayerController.shared.playPreviousAudio()
    }
    
    @IBAction func forwardButtonAction(sender: UIButton) {
        PlayerController.shared.forward()
    }
    
    @IBAction func rewindButtonAction(sender: UIButton) {
        PlayerController.shared.rewind()
    }
    
    @IBAction func playPauseButtonAction(sender: UIButton) {
        let player = PlayerController.shared
        if player.isPlaying() {
            player.pauseAudioPlayer()
        }
        else {
            if player.audioPlayer == nil {
                player.startOrContinuePlaying(bookDetailsModel: bookDetailsModel, delegate: self)
            }
            else {
                player.playAudio()
            }
        }
        playPauseButton.setImage(player.isPlaying() ? #imageLiteral(resourceName: "player_controls_pause") : #imageLiteral(resourceName: "player_controls_play"), for: .normal)
    }
    
    @IBAction func progressSliderAction(sender: UISlider) {
        PlayerController.shared.changeTime(timeInterval: TimeInterval(sender.value))
    }
}

extension PlayerViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return mediaModels.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PlayerItemTableViewCell", for: indexPath) as! PlayerItemTableViewCell
        let isSelected = PlayerController.shared.currentAudiobookIndex == indexPath.row
        cell.setup(mediaModel:  mediaModels[indexPath.row], isPlaying: isSelected)
        return cell
    }
}

extension PlayerViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if mediaModels.count > indexPath.row {
            
            PlayerController.shared.startPlaying(bookDetailsModel: bookDetailsModel, delegate: self, trackIndex: indexPath.row)
        }
    }
}

extension PlayerViewController: PlayerControllerDelegate {
    
    func playerControllerDelegateTrackChanged() {
        
        let player = PlayerController.shared
        progressSlider.maximumValue = Float(player.audioLength)
        
        updateCurrentTime(timeInterval: player.getCurrentTime())
        trackNumberLabel.text = String(format: "player_chapter_number".localized, player.currentAudiobookIndex + 1)
        
        if let currentModel = player.getCurentAudiobookMediaModel() {
            trackTitleLabel.text = currentModel.name
            let time = calculateTimeFromNSTimeInterval(player.audioLength)
            if time.hour.count == 0 {
                trackTimeLabel.text = "\(time.minute):\(time.second)"
            }
            else {
                trackTimeLabel.text = "\(time.hour):\(time.minute):\(time.second)"
            }
        }
        
        playPauseButton.setImage(player.isPlaying() ? #imageLiteral(resourceName: "player_controls_pause") : #imageLiteral(resourceName: "player_controls_play"), for: .normal)
        
        let newSelectedIndex =  IndexPath(row: PlayerController.shared.currentAudiobookIndex, section: 0)
        tableView.selectRow(at: newSelectedIndex, animated: true, scrollPosition: .middle)
        
        refreshButtonsVisibility()
    }
    
    func playerControllerDelegateUpdatePlayerProgress(timeInterval: TimeInterval) {
        updateCurrentTime(timeInterval: timeInterval)
    }

    func playerControllerDelegatePlayStateChanged() {
        playPauseButton.setImage(PlayerController.shared.isPlaying() ? #imageLiteral(resourceName: "player_controls_pause") : #imageLiteral(resourceName: "player_controls_play"), for: .normal)
    }
}

