package com.examtest.demo.telegram;

import com.examtest.demo.config.BotConfig;
import com.examtest.demo.dto.booking.BookingRequestDto;
import com.examtest.demo.dto.booking.BookingResponseDto;
import com.examtest.demo.dto.user.*;
import com.examtest.demo.dto.userorder.UserOrderRequestDto;
import com.examtest.demo.dto.userorder.UserOrderResponseDto;
import com.examtest.demo.exception.NotificationException;
import com.examtest.demo.model.Booking;
import com.examtest.demo.model.User;
import com.examtest.demo.model.UserOrder;
import com.examtest.demo.service.AuthenticationService;
import com.examtest.demo.service.BookingService;
import com.examtest.demo.service.UserOrderService;
import com.examtest.demo.service.UserService;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class TelegramAbilityBot extends AbilityBot {

    private final BotConfig botConfig;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final BookingService bookingService;
    private final UserOrderService userOrderService;
    private final ConcurrentMap<Long, String> userEmails = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, BotState> userStates = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> userPasswords = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> userUpdatedEmails = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> bookingCurrentName = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> bookingName = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, String> bookingDescription = new ConcurrentHashMap<>();

    public TelegramAbilityBot(BotConfig botConfig, AuthenticationService authenticationService, UserService userService, BookingService bookingService, UserOrderService userOrderService) {
        super(botConfig.getBotName(), botConfig.getBotToken());
        this.botConfig = botConfig;
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.userOrderService = userOrderService;
    }

    @PostConstruct
    private void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            throw new NotificationException("Can`t start a telegram bot ", e);
        }
    }

    @Override
    public long creatorId() {
        return 1L;
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    //User Abilities
    public Ability start() {
        return Ability.builder()
                .name("start")
                .info("Welcome message")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    String message = "Welcome! This bot will send you updates about bookings.\nType '/register' if you want to create new account or type '/login' if you already have one.";
                    silent.send(message, ctx.chatId());
                })
                .build();
    }

    public Ability login() {
        return Ability.builder()
                .name("login")
                .info("Login command")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    silent.send("Enter your email", ctx.chatId());
                    userStates.put(ctx.chatId(), BotState.LOGIN_EMAIL);
                })
                .build();
    }

    public Ability register() {
        return Ability.builder()
                .name("register")
                .info("Register command")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    silent.send("Enter your email", ctx.chatId());
                    userStates.put(ctx.chatId(), BotState.REGISTER_EMAIL);
                })
                .build();
    }

    public Ability cancel() {
        return Ability.builder()
                .name("cancel")
                .info("Cancel current operation or logging out")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    userStates.put(ctx.chatId(), BotState.IDLE);
                    userEmails.remove(ctx.chatId());
                    userPasswords.remove(ctx.chatId());
                    userUpdatedEmails.remove(ctx.chatId());
                    bookingCurrentName.remove(ctx.chatId());
                    bookingName.remove(ctx.chatId());
                    bookingDescription.remove(ctx.chatId());
                    silent.send("Current operation was canceled or logged out", ctx.chatId());
                })
                .build();
    }

    public Ability getAllUsers() {
        return Ability.builder()
                .name("getuserlist")
                .info("Send a Userlist (Admin only)")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);
                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);
                            if (currentUser != null && currentUser.getRole() != null && currentUser.getRole().equals(User.Role.ADMIN)) {
                                List<UserBasicDto> users = userService.getAllUsers();
                                StringBuilder response = new StringBuilder("List of users:\n");
                                for (UserBasicDto user : users) {
                                    response.append(user.getEmail()).append("\n");
                                }
                                silent.send(response.toString(), chatId);
                            } else {
                                silent.send("You don't have permission to access this command.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error while fetching users: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Email not found for your chat ID. Please login first.", chatId);
                    }

                })
                .build();
    }

    public Ability deleteUser() {
        return Ability.builder()
                .name("deleteacc")
                .info("Delete a user by email(Admin only) or delete self")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                if (currentUser.getRole().equals(User.Role.CUSTOMER)) {
                                    userService.deleteUser(currentUser.getId());
                                    silent.send("Your account has been successfully deleted.", chatId);
                                } else if (currentUser.getRole().equals(User.Role.ADMIN)) {
                                    silent.send("Enter the email of the user you want to delete", chatId);
                                    userStates.put(chatId, BotState.DELETE_USER_EMAIL);
                                } else {
                                    silent.send("You don't have permission to access this command.", chatId);
                                }
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }

    public Ability DetailedInfo() {
        return Ability.builder()
                .name("info")
                .info("Detailed info about me")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                if (currentUser.getRole().equals(User.Role.CUSTOMER)) {
                                    UserDetailedDto detailedDto = userService.getUserByEmail(currentUser.getEmail());
                                    StringBuilder response = new StringBuilder("Info about your account:\n");
                                    response.append(detailedDto.getEmail());
                                    silent.send(response.toString(), chatId);
                                } else if (currentUser.getRole().equals(User.Role.ADMIN)) {
                                    silent.send("Enter the email of the user you want to check", chatId);
                                    userStates.put(chatId, BotState.INFO_USER_EMAIL);
                                } else {
                                    silent.send("You don't have permission to access this command.", chatId);
                                }
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }

    public Ability updateUser() {
        return Ability.builder()
                .name("update")
                .info("Update user details")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        silent.send("Enter your new email address:", chatId);
                        userStates.put(chatId, BotState.UPDATE_EMAIL);
                    } else {
                        silent.send("You need to log in first to update your details.", chatId);
                    }
                })
                .build();
    }

    public Ability updateUserRole() {
        return Ability.builder()
                .name("changerole")
                .info("Change a user's role (Admin only)")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                if (currentUser.getRole().equals(User.Role.ADMIN)) {
                                    silent.send("Please enter the email of the user whose role you want to change.", chatId);
                                    userStates.put(chatId, BotState.CHANGE_USER_ROLE);
                                } else {
                                    silent.send("You do not have permission to change user roles.", chatId);
                                }
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }


    //Booking Abilities
    public Ability AddBooking() {
        return Ability.builder()
                .name("addbooking")
                .info("Add new booking")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                if (currentUser.getRole().equals(User.Role.ADMIN)) {
                                    silent.send("Enter booking's name", chatId);
                                    userStates.put(chatId, BotState.ADD_BOOKING_NAME);
                                } else {
                                    silent.send("You don't have permission to access this command.", chatId);
                                }
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }

    public Ability UpdateBooking() {
        return Ability.builder()
                .name("updatebooking")
                .info("Update booking")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                if (currentUser.getRole().equals(User.Role.ADMIN)) {
                                    silent.send("Enter current booking's name", chatId);
                                    userStates.put(chatId, BotState.UPDATE_BOOKING);
                                } else {
                                    silent.send("You don't have permission to access this command.", chatId);
                                }
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }

    public Ability getAllBookings() {
        return Ability.builder()
                .name("getbookinglist")
                .info("Send a Bookinglist")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();

                    userStates.put(chatId, BotState.SORT_BOOKING_LIST);
                    silent.send("Sort by (Price, Description or Name)", chatId);
                })
                .build();
    }

    public Ability deleteBooking() {
        return Ability.builder()
                .name("deletebooking")
                .info("Delete a user by email(Admin only)")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                if (currentUser.getRole().equals(User.Role.ADMIN)) {
                                    silent.send("Enter the name of the booking you want to delete", chatId);
                                    userStates.put(chatId, BotState.DELETE_BOOKING);
                                } else {
                                    silent.send("You don't have permission to access this command.", chatId);
                                }
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }


    //UserOrder Abilities
    public Ability AddOrder() {
        return Ability.builder()
                .name("addorder")
                .info("Add new order")
                .locality(Locality.ALL)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> {
                    Long chatId = ctx.chatId();
                    String email = userEmails.get(chatId);

                    if (email != null) {
                        try {
                            User currentUser = userService.getUserByEmailBasic(email);

                            if (currentUser != null) {
                                userStates.put(chatId, BotState.ADD_ORDER_BOOKINGS);
                                silent.send("Booking name", chatId);
                            } else {
                                silent.send("User not found.", chatId);
                            }
                        } catch (Exception e) {
                            silent.send("Error: " + e.getMessage(), chatId);
                        }
                    } else {
                        silent.send("Please login first.", chatId);
                    }
                })
                .build();
    }

    //Other
    public void sendMessageToUser(String text, Long id) {
        try {
            SendMessage sendMessage = new SendMessage(id.toString(), text);
            sendMessage.enableMarkdown(true);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NotificationException("Can`t send message to user with id: " + id, e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        if (message != null && !message.startsWith("/")) {
            BotState currentState = userStates.getOrDefault(chatId, BotState.IDLE);
            switch (currentState) {
                case LOGIN_EMAIL -> handleEmailInput(update);
                case LOGIN_PASSWORD -> handlePasswordInput(update);
                case REGISTER_EMAIL -> handleRegisterEmail(update);
                case REGISTER_PASSWORD -> handleRegisterPassword(update);
                case REGISTER_CONFIRM_PASSWORD -> handleConfirmPassword(update);
                case DELETE_USER_EMAIL -> handleDeleteUserEmail(update);
                case INFO_USER_EMAIL -> handleInfoUserEmail(update);
                case UPDATE_EMAIL -> handleUpdateEmail(update);
                case UPDATE_PASSWORD -> handleUpdatePassword(update);
                case CHANGE_USER_ROLE -> handleUpdateUserRole(update);
                case ADD_BOOKING_NAME -> handleAddBookingName(update);
                case ADD_BOOKING_DESCRIPTION -> handleAddBookingDesc(update);
                case ADD_BOOKING_PRICE -> handleAddBookingPrice(update);
                case UPDATE_BOOKING -> handleUpdateBooking(update);
                case UPDATE_BOOKING_NAME -> handleUpdateBookingName(update);
                case UPDATE_BOOKING_DESCRIPTION -> handleUpdateBookingDescription(update);
                case UPDATE_BOOKING_PRICE -> handleUpdateBookingPrice(update);
                case SORT_BOOKING_LIST -> handleSortBookingList(update);
                case DELETE_BOOKING -> handleDeleteBooking(update);
                case ADD_ORDER_BOOKINGS -> handleAddOrderBookings(update);
            }
        }
        super.onUpdateReceived(update);
    }





    //Handlers for UserOrder
    private void handleAddOrderBookings(Update update){
        Long chatId = update.getMessage().getChatId();
        String bookingName = update.getMessage().getText();
        String email = userEmails.get(chatId);
        if (email != null) {
            try {
                User currentUser = userService.getUserByEmailBasic(email);
                Booking booking = bookingService.getBookingByNameBasic(bookingName);
                if (booking.getOrder() == null){
                    BookingResponseDto responseDto = bookingService.getBookingByName(bookingName);
                    List<BookingResponseDto> list = new ArrayList<>();
                    list.add(responseDto);
                    UserOrderRequestDto userOrderRequestDto = new UserOrderRequestDto(currentUser, list, null);
                    UserOrderResponseDto userOrderResponseDto = userOrderService.addUserOrder(userOrderRequestDto);
                    UserOrder userOrder = userOrderService.getUserOrderByIdBasic(userOrderResponseDto.getId());
                    bookingService.updateBookingBasic(booking, userOrder);
                    List<UserOrder> listorder = new ArrayList<>();
                    listorder.add(userOrder);
                    currentUser.setOrders(listorder);
                    silent.send("Successfully added", chatId);
                } else{
                    silent.send("You cannot order a booking that is already ordered", chatId);
                }

            } catch (Exception e) {
                silent.send("Error: " + e.getMessage(), chatId);
            } finally {
                userStates.put(chatId, BotState.IDLE);
            }
        } else {
            silent.send("You need to log in first before creating an order.", chatId);
        }
    }

    //Handlers for Booking
    private void handleDeleteBooking(Update update){
        Long chatId = update.getMessage().getChatId();
        String nameToDelete = update.getMessage().getText();
        String email = userEmails.get(chatId);

        if (email != null) {
            try {
                User currentUser = userService.getUserByEmailBasic(email);

                if (currentUser != null && currentUser.getRole().equals(User.Role.ADMIN)) {

                    if (nameToDelete != null && !nameToDelete.isEmpty()) {
                        Booking bookingToDelete = bookingService.getBookingByNameBasic(nameToDelete);

                        if (bookingToDelete != null) {
                            bookingService.deleteBooking(bookingToDelete.getId());
                            silent.send("Booking with name " + nameToDelete + " has been successfully deleted.", chatId);
                        } else {
                            silent.send("No booking found with the name: " + nameToDelete, chatId);
                        }
                    } else {
                        silent.send("Please provide a valid name to delete.", chatId);
                    }
                } else {
                    silent.send("You do not have permission to delete bookings. Only admins can perform this action.", chatId);
                }
            } catch (Exception e) {
                silent.send("Error: " + e.getMessage(), chatId);
            } finally {
                userStates.put(chatId, BotState.IDLE);
            }
        } else {
            silent.send("You need to log in first before deleting a booking.", chatId);
        }
    }

    private void handleSortBookingList(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {

            List<BookingResponseDto> bookings = bookingService.getAllBooking();
            if(text.equals("Price")) {
                bookings.sort(Comparator.comparing(BookingResponseDto::getPrice));
            }
            else if(text.equals("Description")) {
                bookings.sort(Comparator.comparing(BookingResponseDto::getDescription));
            }
            else {
                bookings.sort(Comparator.comparing(BookingResponseDto::getName));
            }
            StringBuilder response = new StringBuilder("List of bookings:\n");
            for (BookingResponseDto booking : bookings) {
                response.append(booking.getName()).append("\n").append(booking.getDescription()).append("\n").append(booking.getPrice()).append("$").append("\n\n");
            }
            silent.send(response.toString(), chatId);
        } catch (Exception e) {
            silent.send("Error while fetching booking: " + e.getMessage(), chatId);
        }

    }

    private void handleUpdateBookingPrice(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to update booking", chatId);
                return;
            }
            String currentName = bookingCurrentName.get(chatId);
            BookingResponseDto responseDto = bookingService.getBookingByName(currentName);
            String name = bookingName.get(chatId);
            String description = bookingDescription.get(chatId);
            int price;
            price = Integer.parseInt(text);
            BookingRequestDto requestDto = new BookingRequestDto(name, description, price);
            bookingService.updateBooking(responseDto.getId(), requestDto);
            userStates.put(chatId, BotState.IDLE);
            silent.send("Booking updated successfully", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleUpdateBookingDescription(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to update booking", chatId);
                return;
            }
            bookingDescription.put(chatId, text);
            userStates.put(chatId, BotState.UPDATE_BOOKING_PRICE);
            silent.send("Enter booking's new price without .xx", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleUpdateBookingName(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to update booking", chatId);
                return;
            }
            bookingName.put(chatId, text);
            userStates.put(chatId, BotState.UPDATE_BOOKING_DESCRIPTION);
            silent.send("Enter booking's new description", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleUpdateBooking(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to update booking", chatId);
                return;
            }
            bookingCurrentName.put(chatId, text);
            userStates.put(chatId, BotState.UPDATE_BOOKING_NAME);
            silent.send("Enter booking's new name", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleAddBookingPrice(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to add a new booking", chatId);
                return;
            }
            String name = bookingName.get(chatId);
            String description = bookingDescription.get(chatId);
            int price;
            price = Integer.parseInt(text);
            BookingRequestDto requestDto = new BookingRequestDto(name, description, price);
            bookingService.addBooking(requestDto);
            userStates.put(chatId, BotState.IDLE);
            silent.send("Booking added successfully", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleAddBookingDesc(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to add a new booking", chatId);
                return;
            }
            bookingDescription.put(chatId, text);
            userStates.put(chatId, BotState.ADD_BOOKING_PRICE);
            silent.send("Enter booking's price without .xx", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleAddBookingName(Update update){
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to add a new booking", chatId);
                return;
            }
            bookingName.put(chatId, text);
            userStates.put(chatId, BotState.ADD_BOOKING_DESCRIPTION);
            silent.send("Enter booking's description", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }


    //Handlers for User
    private void handleUpdateUserRole(Update update) {
        Long chatId = update.getMessage().getChatId();
        String targetEmail = update.getMessage().getText();

        try {
            String adminEmail = userEmails.get(chatId);
            if (adminEmail == null) {
                silent.send("Please login first.", chatId);
                return;
            }

            User currentUser = userService.getUserByEmailBasic(adminEmail);
            if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("You do not have permission to change user roles.", chatId);
                return;
            }

            User targetUser = userService.getUserByEmailBasic(targetEmail);
            if (targetUser == null) {
                silent.send("User not found.", chatId);
                return;
            }

            if (targetUser.getRole().equals(User.Role.ADMIN)) {
                silent.send("Cannot change the role of another Admin.", chatId);
                return;
            }

            RoleDto roleDto = new RoleDto(User.Role.ADMIN.name());
            userService.updateUserRole(targetUser.getId(), roleDto);

            silent.send("Successfully updated user role to Admin.", chatId);
        } catch (Exception e) {
            silent.send("Error: " + e.getMessage(), chatId);
        }
    }

    private void handleUpdatePassword(Update update) {
        Long chatId = update.getMessage().getChatId();
        String newPassword = update.getMessage().getText();

        String email = userUpdatedEmails.get(chatId);
        String currentEmail = userEmails.get(chatId);


        try {
            User currentUser = userService.getUserByEmailBasic(currentEmail);

            if (currentUser != null) {
                UserRegistrationDto updateDto = new UserRegistrationDto(email, newPassword, newPassword);
                userService.updateUser(currentUser.getId(), updateDto);

                silent.send("Your details have been updated successfully.", chatId);
            } else {
                silent.send("User not found. Please log in again.", chatId);
            }
        } catch (Exception e) {
            silent.send("Error updating user: " + e.getMessage(), chatId);
        } finally {
            userStates.put(chatId, BotState.IDLE);
            userEmails.put(chatId, email);
            userUpdatedEmails.remove(chatId, email);
        }
    }

    private void handleUpdateEmail(Update update) {
        Long chatId = update.getMessage().getChatId();
        String newEmail = update.getMessage().getText();

        userUpdatedEmails.put(chatId, newEmail);
        userStates.put(chatId, BotState.UPDATE_PASSWORD);

        silent.send("Enter your new password:", chatId);
    }

    private void handleInfoUserEmail(Update update) {
        Long chatId = update.getMessage().getChatId();
        String emailToCheck = update.getMessage().getText();
        String email = userEmails.get(chatId);

        if (email != null) {
            try {
                User currentUser = userService.getUserByEmailBasic(email);

                if (currentUser != null && currentUser.getRole().equals(User.Role.ADMIN)) {

                    if (emailToCheck != null && !emailToCheck.isEmpty()) {
                        UserDetailedDto dtoToCheck = userService.getUserByEmail(emailToCheck);

                        if (dtoToCheck != null) {
                            userService.getUserByEmail(dtoToCheck.getEmail());
                            StringBuilder response = new StringBuilder("Info about your account:\n");
                            response.append(dtoToCheck.getId()).append("\n").append(dtoToCheck.getEmail());
                            silent.send(response.toString(), chatId);
                        } else {
                            silent.send("No user found with the email: " + emailToCheck, chatId);
                        }
                    } else {
                        silent.send("Please provide a valid email to check.", chatId);
                    }
                } else {
                    silent.send("You do not have permission to delete users. Only admins can perform this action.", chatId);
                }
            } catch (Exception e) {
                silent.send("Error: " + e.getMessage(), chatId);
            } finally {
                userStates.put(chatId, BotState.IDLE);
            }
        } else {
            silent.send("You need to log in first before deleting a user.", chatId);
        }
    }

    private void handleDeleteUserEmail(Update update) {
        Long chatId = update.getMessage().getChatId();
        String emailToDelete = update.getMessage().getText();
        String email = userEmails.get(chatId);

        if (email != null) {
            try {
                User currentUser = userService.getUserByEmailBasic(email);

                if (currentUser != null && currentUser.getRole().equals(User.Role.ADMIN)) {

                    if (emailToDelete != null && !emailToDelete.isEmpty()) {
                        User userToDelete = userService.getUserByEmailBasic(emailToDelete);

                        if (userToDelete != null) {
                            userService.deleteUser(userToDelete.getId());
                            silent.send("User with email " + emailToDelete + " has been successfully deleted.", chatId);
                        } else {
                            silent.send("No user found with the email: " + emailToDelete, chatId);
                        }
                    } else {
                        silent.send("Please provide a valid email to delete.", chatId);
                    }
                } else {
                    silent.send("You do not have permission to delete users. Only admins can perform this action.", chatId);
                }
            } catch (Exception e) {
                silent.send("Error: " + e.getMessage(), chatId);
            } finally {
                userStates.put(chatId, BotState.IDLE);
            }
        } else {
            silent.send("You need to log in first before deleting a user.", chatId);
        }
    }

    private void handleEmailInput(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        userEmails.put(chatId, text);
        userStates.put(chatId, BotState.LOGIN_PASSWORD);

        silent.send("Enter your password", chatId);
    }

    private void handlePasswordInput(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        String email = userEmails.get(chatId);

        LoginRequestDto requestDto = new LoginRequestDto(email, text);

        try {
            authenticationService.authenticateWithTelegram(requestDto, chatId);

            silent.send("Success!", chatId);

            userStates.put(chatId, BotState.IDLE);
        } catch (AuthenticationException e) {
            silent.send("Wrong email or password!", chatId);
            silent.send("Try again or press /cancel to exit.", chatId);
        }
    }

    private void handleRegisterEmail(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        userEmails.put(chatId, text);
        userStates.put(chatId, BotState.REGISTER_PASSWORD);

        silent.send("Enter your password", chatId);
    }

    private void handleRegisterPassword(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        userPasswords.put(chatId, text);
        userStates.put(chatId, BotState.REGISTER_CONFIRM_PASSWORD);

        silent.send("Confirm your password", chatId);
    }

    private void handleConfirmPassword(Update update) {
        Long chatId = update.getMessage().getChatId();
        String confirmPassword = update.getMessage().getText();

        String email = userEmails.get(chatId);
        String password = userPasswords.get(chatId);

        if (!password.equals(confirmPassword)) {
            silent.send("Passwords do not match. Try again or press /cancel to exit.", chatId);
            return;
        }

        UserRegistrationDto registrationDto = new UserRegistrationDto(email, password, confirmPassword);

        try {
            userService.register(registrationDto);

            silent.send("Registration successful! You can now use /login", chatId);
            userEmails.remove(chatId);
            userPasswords.remove(chatId);
            userStates.put(chatId, BotState.IDLE);
        } catch (Exception e) {
            silent.send("Registration failed: " + e.getMessage(), chatId);
            silent.send("Try again or press /cancel to exit.", chatId);
        }
    }

}
