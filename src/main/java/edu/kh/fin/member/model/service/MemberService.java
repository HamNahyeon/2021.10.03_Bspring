package edu.kh.fin.member.model.service;

import edu.kh.fin.member.model.vo.Member;

public interface MemberService {

	// 다형성 : 부모타입 참조변수 = 자식객체 
	/* Service interface를 만드는 이유
	 * 
	 * 1. 프로젝트의 규칙성을 부여하기 위해서 
	 * -> 인터페이스를 상속 받으면 동일한 형태의 메서드가 강제된다.
	 * 
	 * 2. 클래스간의 결합도를 낮추고, 유지보수성 향상을 위해
	 * 
	 * 3. Spring의 AOP
	 * -> AOP는 spring-proxy를 이용해서 동작하는데
	 * 	  spring-proxy는 Service 인터페이스를 상속받아 동작함
	 * */
	
	
	// 인터페이스에서 메서드는 모두 묵시적으로 public abstract이다.
	public abstract Member login(Member inputMember);

	/** 아이디 중복검사 Service
	 * @param id
	 * @return result
	 */
	public abstract int idDupCheck(String id);

	/** 회원가입 Service
	 * @param inputMember
	 * @return
	 */
	public abstract int signUp(Member inputMember);

	/** 회원정보수정 Service
	 * @param inputMember
	 * @return
	 */
	public abstract int updateMember(Member inputMember);

	/** 회원비밀번호 변경 Service
	 * @param currentPwd
	 * @param newPwd
	 * @param loginMember
	 * @return
	 */
	public abstract int changePwd(String currentPwd, String newPwd, Member loginMember);

	/** 회원 탈퇴 Service
	 * @param currentPwd
	 * @param agree
	 * @return
	 */
	public abstract int secession(Member loginMember, String currentPwd);
	
}
