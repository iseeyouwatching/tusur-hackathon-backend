//package ru.hits.tusurhackathon.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import ru.hits.tusurhackathon.service.BankAccountService;
//
//import javax.validation.Valid;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/bank-accounts")
//@RequiredArgsConstructor
//@Slf4j
//@Tag(name = "Банковские счета.")
//public class BankAccountRestController {
//
//    private final BankAccountService bankAccountService;
//
//    @Operation(
//            summary = "Посмотреть банковские счета всех клиентов.",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @GetMapping
//    public ResponseEntity<BankAccountsWithPaginationDto> getAllBankAccounts(@RequestParam(defaultValue = "ASC") SortDirection creationDateSortDirection,
//                                                                            @RequestParam(required = false) Boolean isClosed,
//                                                                            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
//                                                                            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
//        return new ResponseEntity<>(bankAccountService.getAllBankAccounts(creationDateSortDirection.toSortDirection(), isClosed, pageNumber, pageSize), HttpStatus.OK);
//    }
//
//    @Operation(
//            summary = "Посмотреть банковские счета клиента.",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @GetMapping("/owner/{id}")
//    public ResponseEntity<BankAccountsWithPaginationDto> getBankAccountsByOwnerId(@PathVariable("id") UUID ownerId,
//                                                                                  @RequestParam(defaultValue = "ASC") SortDirection creationDateSortDirection,
//                                                                                  @RequestParam(required = false) Boolean isClosed,
//                                                                                  @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
//                                                                                  @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
//        return new ResponseEntity<>(bankAccountService.getBankAccountsByOwnerId(ownerId, creationDateSortDirection.toSortDirection(), isClosed, pageNumber, pageSize), HttpStatus.OK);
//    }
//
//    @Operation(
//            summary = "Посмотреть информацию о банковском счёте.",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @GetMapping("/{id}")
//    public ResponseEntity<BankAccountWithoutTransactionsDto> getBankAccountsByOwnerId(@PathVariable("id") UUID bankAccountId) {
//        return new ResponseEntity<>(bankAccountService.getBankAccountById(bankAccountId), HttpStatus.OK);
//    }
//
//    @Operation(
//            summary = "Открыть банковский счёт.",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @PostMapping("/open")
//    public ResponseEntity<BankAccountWithoutTransactionsDto> createBankAccount(@RequestBody @Valid CreateBankAccountDto createBankAccountDto,
//                                                                               @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {
//        return new ResponseEntity<>(bankAccountService.createBankAccount(createBankAccountDto, idempotencyKey), HttpStatus.OK);
//    }
//
//    @Operation(
//            summary = "Закрыть банковский счёт.",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @PostMapping("/{id}/close")
//    public ResponseEntity<BankAccountWithoutTransactionsDto> closeBankAccount(@PathVariable("id") UUID bankAccountId,
//                                                                              @RequestHeader(value = "Idempotency-Key") String idempotencyKey) {
//        return new ResponseEntity<>(bankAccountService.closeBankAccount(bankAccountId, idempotencyKey), HttpStatus.OK);
//    }
//
////    @Operation(
////            summary = "Пополнить банковский счёт.",
////            security = @SecurityRequirement(name = "bearerAuth")
////    )
////    @PostMapping("/{id}/deposit")
////    public ResponseEntity<BankAccountDto> depositMoney(@PathVariable("id") UUID bankAccountId,
////                                                       @RequestBody @Valid DepositMoneyDto depositMoneyDto) {
////        return new ResponseEntity<>(bankAccountService.depositMoney(bankAccountId, depositMoneyDto), HttpStatus.OK);
////    }
////
////    @Operation(
////            summary = "Перевести деньги на счет.",
////            security = @SecurityRequirement(name = "bearerAuth")
////    )
////    @PostMapping("/{id}/transfer")
////    public ResponseEntity<BankAccountDto> transferMoney(@PathVariable("id") UUID toBankAccountId,
////                                                        @RequestBody @Valid TransferMoneyDto transferMoneyDto) {
////        return new ResponseEntity<>(bankAccountService.transferMoney(toBankAccountId, transferMoneyDto), HttpStatus.OK);
////    }
//
////    @Operation(
////            summary = "Снять деньги с банковского счёта.",
////            security = @SecurityRequirement(name = "bearerAuth")
////    )
////    @PostMapping("/{id}/withdraw")
////    public ResponseEntity<BankAccountDto> withdrawMoney(@PathVariable("id") UUID bankAccountId,
////                                                        @RequestBody @Valid WithdrawMoneyDto withdrawMoneyDto) {
////        return new ResponseEntity<>(bankAccountService.withdrawMoney(bankAccountId, withdrawMoneyDto), HttpStatus.OK);
////    }
//
//    @Operation(
//            summary = "Обновить название банковского счёта.",
//            security = @SecurityRequirement(name = "bearerAuth")
//    )
//    @PutMapping("/{id}/name")
//    public ResponseEntity<BankAccountWithoutTransactionsDto> updateBankAccountName(
//            @PathVariable("id") UUID bankAccountId,
//            @RequestBody @Valid UpdateBankAccountNameDto updateBankAccountNameDto) {
//        return new ResponseEntity<>(bankAccountService.updateBankAccountName(bankAccountId, updateBankAccountNameDto), HttpStatus.OK);
//    }
//
//    @GetMapping("/{id}/check-existence")
//    public ResponseEntity<Boolean> checkBankAccountExistence(@PathVariable("id") UUID bankAccountId) {
//        Boolean isExists = bankAccountService.checkBankAccountExistenceById(bankAccountId);
//        return new ResponseEntity<>(isExists, HttpStatus.OK);
//    }
//
//    @PostMapping("/{id}/check-money")
//    public ResponseEntity<Boolean> checkBankAccountAmountOfMoney(@PathVariable("id") UUID bankAccountId,
//                                                                 @RequestBody CheckMoneyDto checkMoneyDto) {
//        Boolean hasMoney = bankAccountService.checkBankAccountAmountOfMoney(bankAccountId, checkMoneyDto);
//        return new ResponseEntity<>(hasMoney, HttpStatus.OK);
//    }
//
//}
