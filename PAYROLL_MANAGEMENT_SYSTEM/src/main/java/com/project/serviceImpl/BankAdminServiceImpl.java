package com.project.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.dto.OrganizationApprovalRequest;
import com.project.dto.OrganizationApprovalResponse;
import com.project.dto.SalaryApprovalRequest;
import com.project.dto.SalaryPaymentDTO;
import com.project.dto.VendorPaymentDTO;
import com.project.entity.Bank;
import com.project.entity.BankAdmin;
import com.project.entity.ClientVendor;
import com.project.entity.Concern;
import com.project.entity.ConcernStatus;
import com.project.entity.Employee;
import com.project.entity.Organization;
import com.project.entity.SalaryPayment;
import com.project.entity.SalaryStatus;
import com.project.entity.User;
import com.project.entity.VendorPayment;
import com.project.entity.VerificationStatus;
import com.project.repo.BankAdminRepo;
import com.project.repo.BankRepo;
import com.project.repo.ClientVendorRepo;
import com.project.repo.ConcernRepo;
import com.project.repo.OrganizationRepo;
import com.project.repo.SalaryPaymentRepo;
import com.project.repo.UserRepo;
import com.project.repo.VendorPaymentRepo;
import com.project.service.BankAdminService;
import com.project.service.EmailService;
import com.project.service.SalaryPaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAdminServiceImpl implements BankAdminService {

    private final BankAdminRepo bankAdminRepo;
    private final OrganizationRepo organizationRepo;
    private final SalaryPaymentRepo salaryPaymentRepo;
    private final ConcernRepo concernRepo;
    private final BankRepo bankRepo;
    private final UserRepo userRepo;
    private final VendorPaymentRepo vendorPaymentRepo;
    private final ClientVendorRepo clientVendorRepo;
    private final SalaryPaymentService salaryPaymentService;
    private final EmailService emailService; 
    private final PasswordEncoder passwordEncoder;

    @Override
    public BankAdmin createBankAdmin(BankAdmin admin) {
        if (bankAdminRepo.count() >= 1) {
            throw new RuntimeException("A Bank Admin already exists. Only one Bank Admin is allowed.");
        }

        if (admin == null) throw new RuntimeException("Admin object cannot be null.");
        if (admin.getEmail() == null || admin.getEmail().isBlank())
            throw new RuntimeException("Email is required for Bank Admin.");
        if (admin.getPassword() == null || admin.getPassword().isBlank())
            throw new RuntimeException("Password is required for Bank Admin.");

        // Generate username
        String username;
        if (admin.getName() != null && !admin.getName().isBlank()) {
            username = admin.getName().contains("@")
                    ? admin.getName().split("@")[0]
                    : admin.getName().toLowerCase().replaceAll("\\s+", "");
        } else {
            username = admin.getEmail().split("@")[0];
        }

        // Check duplicates
        if (userRepo.findByUsername(username).isPresent()) {
            throw new RuntimeException("A user with this username already exists: " + username);
        }
        if (userRepo.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("A user with this email already exists: " + admin.getEmail());
        }

        // Link bank if provided
        if (admin.getBank() != null && admin.getBank().getId() != null) {
            Bank bank = bankRepo.findById(admin.getBank().getId())
                    .orElseThrow(() -> new RuntimeException("Bank not found with ID: " + admin.getBank().getId()));
            admin.setBank(bank);
        }

        // 1️⃣ Store plain password before encoding
        String plainPassword = admin.getPassword();

        // 2️⃣ Create user entity and encode password for DB
        User user = new User();
        user.setUsername(username);
        user.setEmail(admin.getEmail());
        user.setPassword(passwordEncoder.encode(plainPassword)); // store hashed password
        user.setRole("ROLE_BANK_ADMIN");
        userRepo.save(user);

        // 3️⃣ Link and save BankAdmin (with hashed password only)
        admin.setUser(user);
        admin.setPassword(user.getPassword()); // store hashed password
        BankAdmin savedAdmin = bankAdminRepo.save(admin);

        String emailBody = String.format(
        	    "Dear %s,\r\n\r\n" +
        	    "Your Bank Admin account has been created successfully.\r\n\r\n" +
        	    "Login Credentials:\r\n" +
        	    "Username: %s\r\n" +
        	    "Password: %s\r\n\r\n" +
        	    ".\r\n\r\n" +
        	    "Regards,\r\nPayroll Management System",
        	    savedAdmin.getName() != null ? savedAdmin.getName() : "Bank Admin",
        	    username,
        	    plainPassword
        	);

        emailService.sendEmail(savedAdmin.getEmail(), "Bank Admin Account Created", emailBody);

        return savedAdmin;
    }

    // ✅ Get all Bank Admins
    @Override
    public List<BankAdmin> getAllBankAdmins() {
        return bankAdminRepo.findAll();
    }

    // ✅ Update Bank Admin
    @Override
    public BankAdmin updateBankAdmin(Long id, BankAdmin updatedAdmin) {
        BankAdmin existing = getBankAdminById(id);

        if (updatedAdmin.getName() != null)
            existing.setName(updatedAdmin.getName());
        if (updatedAdmin.getEmail() != null)
            existing.setEmail(updatedAdmin.getEmail());
        if (updatedAdmin.getContactNumber() != null)
            existing.setContactNumber(updatedAdmin.getContactNumber());

        // ✅ Update password if provided
        if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
        }

        // ✅ Sync User entity
        if (existing.getUser() != null) {
            User user = existing.getUser();

            if (updatedAdmin.getEmail() != null)
                user.setEmail(updatedAdmin.getEmail());

            // Keep username fixed as the pre-"@" email part
            String newUsername = user.getEmail().split("@")[0];
            user.setUsername(newUsername);

            if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isBlank())
                user.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));

            userRepo.save(user);
            existing.setUser(user);
        }

        return bankAdminRepo.save(existing);
    }

    // ✅ Delete Bank Admin
    @Override
    public void deleteBankAdmin(Long id) {
        BankAdmin admin = getBankAdminById(id);

        if (admin.getUser() != null) {
            userRepo.delete(admin.getUser());
        }

        bankAdminRepo.delete(admin);
    }

    // ✅ Get Bank Admin by ID
    @Override
    public BankAdmin getBankAdminById(Long id) {
        return bankAdminRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Bank Admin not found with ID: " + id));
    }

    // ✅ Get all Organizations (Bank Admin view)
    public List<Organization> getAllOrganizations() {
        return organizationRepo.findAll();
    }

    // ✅ Get Organization by ID
    public Organization getOrganizationById(Long orgId) {
        return organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + orgId));
    }

    // ✅ Approve Organization
    @Override
    public Organization approveOrganization(Long orgId, Long adminId) {
        Organization org = getOrganizationById(orgId);
        BankAdmin admin = getBankAdminById(adminId);

        org.setVerificationStatus(VerificationStatus.APPROVED);
        org.setVerifiedBy(admin);
        return organizationRepo.save(org);
    }

    // ✅ Reject Organization
    @Override
    public Organization rejectOrganization(Long orgId, Long adminId, String remarks) {
        Organization org = getOrganizationById(orgId);
        BankAdmin admin = getBankAdminById(adminId);

        org.setVerificationStatus(VerificationStatus.REJECTED);
        org.setVerifiedBy(admin);
        // Optional: save remarks if field exists
        return organizationRepo.save(org);
    }

    @Override
    public OrganizationApprovalResponse verifyOrganization(Long orgId, OrganizationApprovalRequest request) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + orgId));

        BankAdmin admin = bankAdminRepo.findById(request.getAdminId())
                .orElseThrow(() -> new RuntimeException("Bank Admin not found with ID: " + request.getAdminId()));

        org.setVerifiedBy(admin);
        org.setVerificationStatus(request.getStatus());

        // ❌ Rejection
        if (request.getStatus() == VerificationStatus.REJECTED) {
            organizationRepo.save(org);

            // Send rejection email
            String rejectBody = String.format(
                    "Dear %s,\n\n" +
                            "Your organization '%s' has been rejected.\n" +
                            "Remarks: %s\n\n" +
                            "Regards,\nPayroll Management System",
                    org.getOrgName(),
                    org.getOrgName(),
                    request.getRemarks() != null ? request.getRemarks() : "No remarks provided"
            );
            emailService.sendEmail(org.getContactEmail(), "Organization Verification Status", rejectBody);

            return new OrganizationApprovalResponse(
                    "Organization '" + org.getOrgName() + "' has been rejected. Remarks: " + request.getRemarks(),
                    null,
                    null
            );
        }

        // ✅ Approval
        if (request.getStatus() == VerificationStatus.APPROVED) {
            String username = request.getUsername();
            String password = request.getPassword();

            if (username == null || username.isBlank())
                throw new RuntimeException("Username is required for approval.");
            if (password == null || password.isBlank())
                throw new RuntimeException("Password is required for approval.");

            // Duplicate checks
            if (userRepo.findByUsername(username).isPresent()) {
                throw new RuntimeException("Username already exists: " + username);
            }
            if (userRepo.findByEmail(org.getContactEmail()).isPresent()) {
                throw new RuntimeException("Email already exists: " + org.getContactEmail());
            }

            // Create org login
            User user = new User();
            user.setUsername(username);
            user.setEmail(org.getContactEmail());
            user.setPassword(passwordEncoder.encode(password));
            user.setRole("ROLE_MANAGER"); // Org admin role
            user.setOrganization(org);

            userRepo.save(user);
            organizationRepo.save(org);

            // ✅ Send approval email with username, email, and password
            String approveBody = String.format(
                    "Dear %s,\n\n" +
                            "Your organization '%s' has been approved.\n\n" +
                            "Login credentials for your account:\n" +
                            "Username: %s\n" +
                            "Email: %s\n" +
                            "Password: %s\n\n" +
                            ".\n\n" +
                            "Regards,\nPayroll Management System",
                    org.getOrgName(),
                    org.getOrgName(),
                    username,
                    org.getContactEmail(),
                    password
            );

            emailService.sendEmail(org.getContactEmail(), "Organization Approved & Account Created", approveBody);

            return new OrganizationApprovalResponse(
                    "Organization '" + org.getOrgName() + "' approved successfully. Login created.",
                    username,
                    password
            );
        }

        throw new RuntimeException("Invalid verification status.");
    }
    
 // Salary disbursal request
    @Override
    public List<SalaryPaymentDTO> createSalaryDisbursalRequest(Long organizationId, String month) {
        return salaryPaymentService.createSalaryDisbursal(organizationId, month);
    }

    @Override
    public SalaryPaymentDTO rejectSalaryPayment(Long paymentId, Long adminId, String remarks) {
        return salaryPaymentService.rejectSalaryPayment(paymentId, adminId, remarks);
    }
    
    @Override
    public List<SalaryPaymentDTO> approveSalaryBatch(Long organizationId, Long adminId, String month, boolean approved, String remarks) {
        List<SalaryPayment> payments = salaryPaymentRepo.findByOrganizationIdAndSalaryMonth(organizationId, month);

        if (payments.isEmpty()) {
            throw new RuntimeException("No salary payments found for organization " + organizationId + " and month " + month);
        }

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank Admin not found"));

        List<SalaryPaymentDTO> result = payments.stream().map(payment -> {
            if (approved) {
                payment.setStatus(SalaryStatus.COMPLETED);
            } else {
                payment.setStatus(SalaryStatus.FAILED);
            }

            payment.setVerifiedBy(admin);
            payment.setRemarks(remarks);
            salaryPaymentRepo.save(payment);

            // ✅ Send email if approved
            if (approved && payment.getEmployee() != null && payment.getEmployee().getEmail() != null
                    && payment.getEmployee().getSalaryStructure() != null) {

                var employee = payment.getEmployee();
                var salary = employee.getSalaryStructure();

                StringBuilder salaryDetails = new StringBuilder();
                salaryDetails.append("Basic: ").append(salary.getBasic()).append("\n")
                             .append("HRA: ").append(salary.getHra()).append("\n")
                             .append("DA: ").append(salary.getDa()).append("\n")
                             .append("PF: ").append(salary.getPf()).append("\n")
                             .append("Allowances: ").append(salary.getAllowances()).append("\n")
                             .append("Net Salary: ").append(salary.getNetSalary()).append("\n");

                String emailBody = String.format(
                        "Dear %s,\n\n" +
                        "Your salary for %s has been %s by the Bank Admin.\n\n" +
                        "Salary Details:\n%s\n" +
                        "Remarks: %s\n" +
                        "Payment Date: %s\n" +
                        "Status: %s\n\n" +
                        "Regards,\nPayroll Management System",
                        employee.getFullName(),
                        payment.getSalaryMonth(),
                        approved ? "approved" : "rejected",
                        salaryDetails.toString(),
                        remarks != null ? remarks : "None",
                        payment.getPaymentDate(),
                        payment.getStatus().name()
                );

                emailService.sendEmail(employee.getEmail(), "Salary Payment " + (approved ? "Approved" : "Rejected"), emailBody);
            }

            return SalaryPaymentDTO.builder()
                    .id(payment.getId())
                    .amount(payment.getAmount())
                    .salaryMonth(payment.getSalaryMonth())
                    .status(payment.getStatus().name())
                    .employeeId(payment.getEmployee().getId())
                    .organizationId(payment.getOrganization().getId())
                    .verifiedByAdminId(payment.getVerifiedBy().getId())
                    .build();
        }).collect(Collectors.toList());

        return result;
    }

    
    @Override
    public VendorPaymentDTO requestVendorPayment(Long orgId, Long vendorId, Double amount, String remarks) {
        Organization org = organizationRepo.findById(orgId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        ClientVendor vendor = clientVendorRepo.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        VendorPayment payment = VendorPayment.builder()
                .organization(org)
                .vendor(vendor)
                .amount(amount)
                .status(SalaryStatus.PENDING)
                .remarks(remarks)
                .requestDate(LocalDateTime.now())
                .build();

        vendorPaymentRepo.save(payment);

        return VendorPaymentDTO.builder()
                .id(payment.getId())
                .vendorId(vendor.getCvId())
                .vendorName(vendor.getName())
                .organizationId(org.getId())
                .amount(amount)
                .status(payment.getStatus().name())
                .remarks(remarks)
                .verifiedByAdminId(null)
                .requestDate(payment.getRequestDate())
                .build();
    }

    @Override
    public VendorPaymentDTO approveVendorPayment(Long paymentId, Long adminId) {
        VendorPayment payment = vendorPaymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank Admin not found"));

        payment.setStatus(SalaryStatus.COMPLETED);
        payment.setVerifiedBy(admin);
        vendorPaymentRepo.save(payment);

        // ✅ Send payment email to vendor
        ClientVendor vendor = payment.getVendor();
        Organization org = payment.getOrganization();

        if (vendor != null && vendor.getEmail() != null) {
            String emailBody = String.format(
                    "Dear %s,\n\n" +
                    "Your payment from organization '%s' has been approved by the Bank Admin.\n\n" +
                    "Payment Details:\n" +
                    "Vendor Name: %s\n" +
                    "Organization: %s\n" +
                    "Amount: %.2f\n" +
                    "Status: %s\n" +
                    "Payment Date: %s\n\n" +
                    "Remarks: %s\n\n" +
                    "Regards,\nPayroll Management System",
                    vendor.getName(),
                    org.getOrgName(),
                    vendor.getName(),
                    org.getOrgName(),
                    payment.getAmount(),
                    payment.getStatus().name(),
                    payment.getRequestDate(),
                    payment.getRemarks() != null ? payment.getRemarks() : "None"
            );

            emailService.sendEmail(vendor.getEmail(), "Vendor Payment Approved", emailBody);
        }

        return VendorPaymentDTO.builder()
                .id(payment.getId())
                .vendorId(vendor.getCvId())
                .vendorName(vendor.getName())
                .organizationId(org.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .remarks(payment.getRemarks())
                .verifiedByAdminId(admin.getId())
                .requestDate(payment.getRequestDate())
                .build();
    }


    @Override
    public VendorPaymentDTO rejectVendorPayment(Long paymentId, Long adminId, String remarks) {
        VendorPayment payment = vendorPaymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank Admin not found"));

        payment.setStatus(SalaryStatus.FAILED);
        payment.setRemarks(remarks);
        payment.setVerifiedBy(admin);
        vendorPaymentRepo.save(payment);

        return VendorPaymentDTO.builder()
                .id(payment.getId())
                .vendorId(payment.getVendor().getCvId())
                .vendorName(payment.getVendor().getName())
                .organizationId(payment.getOrganization().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .remarks(payment.getRemarks())
                .verifiedByAdminId(admin.getId())
                .requestDate(payment.getRequestDate())
                .build();
    }
    
    @Override
    public Concern resolveConcern(Long concernId, Long adminId, String remarks) {
        Concern concern = concernRepo.findById(concernId)
                .orElseThrow(() -> new RuntimeException("Concern not found"));

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank admin not found"));

        concern.setHandledBy(admin);
        concern.setStatus(ConcernStatus.RESOLVED);
        concern.setResolvedAt(LocalDateTime.now());
        concern.setResolutionRemarks(remarks);

        return concernRepo.save(concern);
    }

    @Override
    public Concern rejectConcern(Long concernId, Long adminId, String remarks) {
        Concern concern = concernRepo.findById(concernId)
                .orElseThrow(() -> new RuntimeException("Concern not found"));

        BankAdmin admin = bankAdminRepo.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Bank admin not found"));

        concern.setHandledBy(admin);
        concern.setStatus(ConcernStatus.REJECTED);
        concern.setResolvedAt(LocalDateTime.now());
        concern.setResolutionRemarks(remarks);

        return concernRepo.save(concern);
    }


    @Override
    public List<Concern> getAllConcerns() {
        return concernRepo.findAll();
    }
    
    public SalaryPaymentDTO approveSalaryPayment(Long paymentId, Long adminId) 
    { 
    	SalaryPaymentDTO paymentDTO = salaryPaymentService.approveSalaryPayment(paymentId, adminId);
    	// Fetch full SalaryPayment entity to access employee and salary structure 
    	SalaryPayment payment = salaryPaymentRepo.findById(paymentId) 
    			.orElseThrow(() -> new RuntimeException("Payment not found"));
    	Employee employee = payment.getEmployee(); 
    	if (employee != null && employee.getEmail() != null && employee.getSalaryStructure() != null)
    	{ 
    		StringBuilder salaryDetails = new StringBuilder(); 
    		salaryDetails.append("Basic: ")
    		.append(employee.getSalaryStructure()
    				.getBasic()).append("\n") 
    		.append("HRA: ")
    		.append(employee.getSalaryStructure().getHra()).append("\n") .append("DA: ")
    		.append(employee.getSalaryStructure().getDa()).append("\n") .append("PF: ")
    		.append(employee.getSalaryStructure().getPf()).append("\n") .append("Allowances: ")
    		.append(employee.getSalaryStructure().getAllowances()).append("\n") .append("Net Salary: ")
    		.append(employee.getSalaryStructure().getNetSalary()).append("\n"); 
    		String emailBody = String.format( "Dear %s,\n\n" + "Your salary for %s has been approved by the Bank Admin.\n\n" 
    		+ "Salary Details:\n%s\n" + "Payment Date: %s\n" + "Status: %s\n\n" 
    		+ "Regards,\nPayroll Management System", employee.getFullName(), payment.getSalaryMonth(), salaryDetails.toString(),
    		payment.getPaymentDate(), payment.getStatus().name() ); 
    		emailService.sendEmail(employee.getEmail(), "Salary Payment Approved", emailBody); 
    		} return paymentDTO;
    	}
    
}
