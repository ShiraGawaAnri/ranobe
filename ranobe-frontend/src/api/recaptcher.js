import ajax from '@/service/ajax';

export async function SendRecaptchaResponse(data) {
  return await ajax({
    url: '/recaptcha/google',
    type: 'post',
    data,
  });
}

export default { SendRecaptchaResponse };
