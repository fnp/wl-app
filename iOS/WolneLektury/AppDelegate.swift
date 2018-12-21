//
//  AppDelegate.swift
//  WolneLektury
//
//  Created by Pawel Dabrowski on 29/03/2018.
//  Copyright © 2018 Fundacja Nowoczesna Polska. All rights reserved.
//

import UIKit
import OAuthSwift
import SafariServices
import MBProgressHUD
import MatomoTracker
import Fabric
import Crashlytics
import Firebase
import UserNotifications

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var window: UIWindow?
    var windowHud: MBProgressHUD?
    var mainNavigator: MainNavigator!
    var syncManager = SyncManager()
    weak var sideMenuNavigationController: UINavigationController?
    weak var safariParentViewController: UIViewController?
    let gcmMessageIDKey = "gcm.message_id"

    func showWindowHud(){
        
        guard let window = window else {return}
        
        if let windowHud = windowHud {
            windowHud.show(animated: true)
        }
        else{
            windowHud = MBProgressHUD.showAdded(to: window, animated: true)
        }
    }
    
    func hideWindowHud(){
        windowHud?.hide(animated: true)
    }
    
    var backgroundSessionCompletionHandler : (() -> Void)?    

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        Fabric.with([Crashlytics.self])
        FirebaseApp.configure()

        mainNavigator = MainNavigator(window: window!)
        setCustomAppearance()
        
        if syncManager.isLoggedIn(){
            fetchUsername()
        }
        
//        configureMessaging(application: application)
        
        return true
    }
    
    func configureMessaging(application: UIApplication) {
        // [START set_messaging_delegate]
        Messaging.messaging().delegate = self
        // [END set_messaging_delegate]
        
        // Register for remote notifications. This shows a permission dialog on first run, to
        // show the dialog at a more appropriate time move this registration accordingly.
        // [START register_for_notifications]
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        
        application.registerForRemoteNotifications()
        
        // [END register_for_notifications]
    }
    
    func setCustomAppearance() {
        
        UINavigationBar.appearance().isTranslucent = false
        UINavigationBar.appearance().tintColor = .white
        UINavigationBar.appearance().titleTextAttributes = [NSAttributedStringKey.foregroundColor : UIColor.white]
        UINavigationBar.appearance().barTintColor = Constants.Colors.navbarBgColor()
        UINavigationBar.appearance().barStyle = .blackOpaque
        UIApplication.shared.statusBarStyle = .lightContent
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
        mainNavigator.updateSettingsViewIfPresented()
    }

    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    func application(_ application: UIApplication, handleEventsForBackgroundURLSession identifier: String, completionHandler: @escaping () -> Void) {
        backgroundSessionCompletionHandler = completionHandler
    }
    
    func application(_ app: UIApplication, open url: URL, options: [UIApplicationOpenURLOptionsKey : Any] = [:]) -> Bool {
        
        applicationHandle(url: url)
        return true
    }
    
    private func applicationHandle(url: URL) {
        if url.host == Constants.callbackOauthHost {
            showWindowHud()
            syncManager.accessToken { (result) in
                self.hideWindowHud()
                switch result {
                case .success(let model):
                    self.syncManager.updateUserCredentials(oAuthTokenModel: (model as! OAuthTokenModel))
                    self.safariParentViewController?.presentedViewController?.dismiss(animated: true, completion: nil)
                    self.mainNavigator.presentLibrary(dismissSideMenu: true)
                    self.fetchUsername()
                    break
                case .failure/*(let error)*/:
                    break
                }
            }
        }
        else if url.host == Constants.callbackPaypalSuccessHost {
            self.safariParentViewController?.presentedViewController?.dismiss(animated: true, completion: nil)
            safariParentViewController?.presentToast(message: "premium_purchase_succeeded".localized)
            DatabaseManager.shared.setUserPremium()
            mainNavigator.reset()
        }
        else if url.host == Constants.callbackPaypalErrorHost {
            self.safariParentViewController?.presentedViewController?.dismiss(animated: true, completion: nil)
            safariParentViewController?.presentToast(message: "premium_purchase_failed".localized)
        }
    }
    
    private func fetchUsername(){
        showWindowHud()
        self.syncManager.getUsername(completionHandler: { (result) in
            self.hideWindowHud()
            switch result {
            case .success(let model):
                DatabaseManager.shared.updateUser(usernameModel: model as? UsernameModel)
                self.mainNavigator.setLoggedIn()
                break
            case .failure/*(let error)*/:
                //We can download username later
                self.mainNavigator.setLoggedIn()
                break
            }
        })
    }
    
    func login(fromViewController: UIViewController){
        let oauthswift = OAuth1Swift(
            consumerKey:    Config.CONSUMER_KEY,
            consumerSecret: Config.CONSUMER_SECRET,
            requestTokenUrl: Config.OAUTH_REQUEST_TOKEN,
            authorizeUrl:    Config.OAUTH_AUTHORIZE,
            accessTokenUrl:  Config.OAUTH_ACCESS_TOKEN
        )
        
        safariParentViewController = fromViewController
        
        let handler = SafariURLHandler(viewController: fromViewController, oauthSwift: oauthswift)
        handler.presentCompletion = {
            print("Safari presented")
        }
        handler.dismissCompletion = {
            print("Safari dismissed")
        }
        handler.factory = { url in
            let controller = SFSafariViewController(url: url)
            // Customize it, for instance
            if #available(iOS 10.0, *) {
                //  controller.preferredBarTintColor = UIColor.red
            }
            return controller
        }
        
        oauthswift.authorizeURLHandler = handler
        showWindowHud()
        syncManager.requestToken { (result) in
            self.hideWindowHud()
            switch result {
            case .success(let model):
                let url = URL(string: String(format: Constants.authorizationUrlFormat, (model as! OAuthTokenModel).token))
                oauthswift.authorizeURLHandler.handle(url!)
                break
            case .failure/*(let error)*/:
                break
            }
        }
    }
    
    func presentPaypal(fromViewController: UIViewController) {
        
        let controller = SFSafariViewController(url: URL(string: Constants.webPaypalFormUrl)!)
        safariParentViewController = fromViewController
        fromViewController.present(controller, animated: true, completion: nil)
    }
    
    
    // ############################################
    // MARK: - Messaging
    // ############################################
    
    // [START receive_message]
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any]) {
        // If you are receiving a notification message while your app is in the background,
        // this callback will not be fired till the user taps on the notification launching the application.
        // TODO: Handle data of notification
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        // Print full message.
        print(userInfo)
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        // If you are receiving a notification message while your app is in the background,
        // this callback will not be fired till the user taps on the notification launching the application.
        // TODO: Handle data of notification
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        // Print full message.
        print(userInfo)
        
        completionHandler(UIBackgroundFetchResult.newData)
    }
    // [END receive_message]
    
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Unable to register for remote notifications: \(error.localizedDescription)")
    }
    
    // This function is added here only for debugging purposes, and can be removed if swizzling is enabled.
    // If swizzling is disabled then this function must be implemented so that the APNs token can be paired to
    // the FCM registration token.
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        print("APNs token retrieved: \(deviceToken)")
        
        // With swizzling disabled you must set the APNs token here.
        // Messaging.messaging().apnsToken = deviceToken
    }
}

// [START ios_10_message_handling]
@available(iOS 10, *)
extension AppDelegate : UNUserNotificationCenterDelegate {
    
    // Receive displayed notifications for iOS 10 devices.
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        
        // With swizzling disabled you must let Messaging know about the message, for Analytics
        // Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        // Print full message.
        print(userInfo)
        
        // Change this to your preferred presentation option
        completionHandler([])
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        // Print message ID.
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        
        // Print full message.
        print(userInfo)
        
        completionHandler()
    }
}
// [END ios_10_message_handling]


extension AppDelegate : MessagingDelegate {
    // [START refresh_token]
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("Firebase registration token: \(fcmToken)")
        
        let dataDict:[String: String] = ["token": fcmToken]
        
        Messaging.messaging().subscribe(toTopic: "wolnelektury")
//        NotificationCenter.default.post(name: Notification.Name("FCMToken"), object: nil, userInfo: dataDict)
        // TODO: If necessary send token to application server.
        // Note: This callback is fired at each app startup and whenever a new token is generated.
    }
    // [END refresh_token]
    
    // [START ios_10_data_message]
    // Receive data messages on iOS 10+ directly from FCM (bypassing APNs) when the app is in the foreground.
    // To enable direct data messages, you can set Messaging.messaging().shouldEstablishDirectChannel to true.
    func messaging(_ messaging: Messaging, didReceive remoteMessage: MessagingRemoteMessage) {
        print("Received data message: \(remoteMessage.appData)")
    }
    // [END ios_10_data_message]
}
